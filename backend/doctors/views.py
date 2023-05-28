import django_filters
from django.shortcuts import render, get_object_or_404
from django_filters.rest_framework import DjangoFilterBackend
from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework import viewsets, status
from rest_framework.views import APIView
from rest_framework.viewsets import ReadOnlyModelViewSet

from patients.models import Patient, Measurement
from patients.serializers import PatientSerializer, MeasurementSerializer
from .models import Treatment, PatientDoctor, Doctor
from .serializers import DoctorPatientDetailSerializer, DoctorPatientListSerializer, \
    TreatmentListSerializer, TreatmentCreateSerializer
from rest_framework.response import Response


class PatientDoctorStatusView(APIView):
    def put(self, request, pk):
        doctor = Doctor.objects.get(user=request.user)
        patient = get_object_or_404(Patient, pk=pk)
        relation = PatientDoctor.objects.get(patient=patient, doctor=doctor)
        if relation.status:
            relation.status = False
        else:
            relation.status = True
        relation.save()
        return Response(status=status.HTTP_200_OK)
        # if (patient := Patient.objects.filter(pk=pk)) is None:
        #     return Response(data={'model': 'patient'}, status=status.HTTP_404_NOT_FOUND)
        # if (doctor := Doctor.objects.filter(user=request.user)) is None:
        #     return Response(data={'model': 'doctor'}, status=status.HTTP_404_NOT_FOUND)
        #
        # try:
        #     relation = PatientDoctor.objects.get(patient=patient, doctor=doctor)
        #     relation.status = False
        #     relation.save()
        #     return Response({"status": "Status changed"})
        # except PatientDoctor.DoesNotExist:
        #     return Response(data={'model': 'pd'}, status=status.HTTP_404_NOT_FOUND)


class ShowPatientsView(APIView):
    @swagger_auto_schema(
        manual_parameters=[
            openapi.Parameter('status', openapi.IN_QUERY, description='Статус True/False', type=openapi.TYPE_STRING)
        ],
        responses={
            200: openapi.Response('response description', DoctorPatientListSerializer)
        }
    )
    def get(self, request):
        doctor = Doctor.objects.get(user=self.request.user)
        status = self.request.query_params.get('status')
        patients = Patient.objects.filter(doctors_patient__doctor=doctor, doctors_patient__status__exact=status)
        serializer = DoctorPatientListSerializer(patients, many=True)
        return Response(serializer.data)


class ShowDetailView(APIView):
    @swagger_auto_schema(responses={
        200: openapi.Response('response description', DoctorPatientDetailSerializer)
    })
    def get(self, request, pk):
        patient = Patient.objects.get(pk=pk)
        serializer = DoctorPatientDetailSerializer(patient)
        return Response(serializer.data)


class PatientTreatmentsView(APIView):
    @swagger_auto_schema(responses={
        200: openapi.Response('response description', TreatmentListSerializer)
    })
    def get(self, request, pk):
        patient = Treatment.objects.filter(patient_id=pk)
        serializer = TreatmentListSerializer(patient, many=True)
        return Response(serializer.data)

    @swagger_auto_schema(request_body=openapi.Schema(type=openapi.TYPE_OBJECT,
                                                     properties={
                                                         "message": openapi.Schema(type=openapi.TYPE_STRING,
                                                                                   description='Message')
                                                     }), responses={
        200: openapi.Response('response description', TreatmentCreateSerializer)
    })
    def post(self, request, pk):
        try:
            Treatment.objects.create(patient_id=pk, message=request.data['message'])
            return Response(status=status.HTTP_201_CREATED)
        except:
            return Response(status=status.HTTP_400_BAD_REQUEST)

# class ShowMeasurementsView(APIView):
#     def get(self, pk):
#         queryset = Measurement.objects.filter(patient_id=pk)
#         serializer= MeasurementSerializer(queryset, many=True)
#         return

