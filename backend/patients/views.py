from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework import viewsets, status
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from doctors.models import Treatment
from doctors.serializers import TreatmentSerializer
from patients.models import Measurement, Patient
from patients.serializers import MeasurementSerializer, PatientSerializer, DeviceSerializer


class PatientView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(responses={
        200: openapi.Response('response description', PatientSerializer)
    })
    def get(self, request):
        queryset = Patient.objects.get(user=self.request.user)
        serializer = PatientSerializer(queryset)
        return Response(serializer.data)


class DeviceView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        request_body=DeviceSerializer,
        responses={200: openapi.Response('response description', PatientSerializer)}
    )
    def put(self, request):
        patient = Patient.objects.get(user=self.request.user)
        device = request.data.get('device')
        patient.device = device
        patient.save()
        serializer = PatientSerializer(patient)
        return Response(serializer.data, status=status.HTTP_200_OK)


class MeasurementsView(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Measurement.objects.all()
    serializer_class = MeasurementSerializer

    def perform_create(self, serializer):
        patient = Patient.objects.get(user=self.request.user)
        serializer.save(patient=patient)

    def get_queryset(self):
        q = Measurement.objects.filter(patient=Patient.objects.get(user=self.request.user))
        return q


class TreatmentsView(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Treatment.objects.all()
    serializer_class = TreatmentSerializer

    def get_queryset(self):
        q = Treatment.objects.filter(patient=Patient.objects.get(user=self.request.user))
        return q
