from django.shortcuts import render, get_object_or_404
from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework import status
from rest_framework.views import APIView
from rest_framework.permissions import IsAuthenticated
from patients.models import Patient, Measurement
from patients.serializers import MeasurementSerializer
from .models import Treatment, PatientDoctor, Doctor
from .serializers import (
    DoctorPatientDetailSerializer,
    DoctorPatientListSerializer,
    TreatmentListSerializer,
    TreatmentCreateSerializer
)
from rest_framework.response import Response
from rest_framework.exceptions import NotAuthenticated, server_error, NotFound


class PatientDoctorStatusView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        responses={
            200: openapi.Response('Success'),
            401: openapi.Response('Unauthorized'),
            404: openapi.Response('Not Found'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def put(self, request, pk):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            doctor = Doctor.objects.get(user=request.user)
            patient = get_object_or_404(Patient, pk=pk)
            try:
                relation = PatientDoctor.objects.get(patient=patient, doctor=doctor)
            except Exception as e:
                raise NotFound()
            if relation.status:
                relation.status = False
            else:
                relation.status = True
            relation.save()
            return Response(status=status.HTTP_200_OK)
        except Exception as e:
            return server_error(request)


class ShowPatientsView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        manual_parameters=[
            openapi.Parameter(
                'status',
                openapi.IN_QUERY,
                description='Статус True/False',
                type=openapi.TYPE_STRING
            )
        ],
        responses={
            200: openapi.Response('Success', DoctorPatientListSerializer),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def get(self, request):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            doctor = Doctor.objects.get(user=self.request.user)
            status = self.request.query_params.get('status')
            patients = Patient.objects.filter(
                doctors_patient__doctor=doctor,
                doctors_patient__status__exact=status
            )
            serializer = DoctorPatientListSerializer(patients, many=True)
            return Response(serializer.data)
        except Exception as e:
            return server_error(request)


class ShowDetailView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        responses={
            200: openapi.Response('Success', DoctorPatientDetailSerializer),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def get(self, request, pk):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            patient = Patient.objects.get(pk=pk)
            serializer = DoctorPatientDetailSerializer(patient)
            return Response(serializer.data)
        except Exception as e:
            return server_error(request)


class PatientTreatmentsView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        responses={
            200: openapi.Response('Success', TreatmentListSerializer),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def get(self, request, pk):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            patient = Treatment.objects.filter(patient_id=pk)
            serializer = TreatmentListSerializer(patient, many=True)
            return Response(serializer.data)
        except Exception as e:
            return server_error(request)

    @swagger_auto_schema(
        request_body=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={"message": openapi.Schema(type=openapi.TYPE_STRING, description='Message')}
        ),
        responses={
            200: openapi.Response('response description', TreatmentCreateSerializer),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def post(self, request, pk):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            patient = get_object_or_404(Patient, pk=pk)
            Treatment.objects.create(patient, message=request.data['message'])
            return Response(status=status.HTTP_201_CREATED)
        except Exception as e:
            return server_error(request)


class ShowMeasurementsView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        responses={
            200: openapi.Response('response description', MeasurementSerializer),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def get(self, request, pk):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            queryset = Measurement.objects.filter(patient_id=pk)
            serializer = MeasurementSerializer(queryset, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except Exception as e:
            return server_error(request)
