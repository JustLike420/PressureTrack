from rest_framework import viewsets, status
from rest_framework.views import APIView
from rest_framework.response import Response
from doctors.models import Doctor
from patients.models import Measurement, Patient
from patients.serializers import MeasurementSerializer, PatientSerializer


class PatientView(APIView):

    def get(self, request):
        queryset = Patient.objects.get(user=self.request.user)
        serializer = PatientSerializer(queryset)
        return Response(serializer.data)


class DeviceView(APIView):

    def put(self, request):
        patient = Patient.objects.get(user=self.request.user)
        device = request.data.get('device')
        patient.device = device
        patient.save()
        serializer = PatientSerializer(patient)
        return Response(serializer.data, status=status.HTTP_200_OK)


class MeasurementsView(viewsets.ModelViewSet):
    queryset = Measurement.objects.all()
    serializer_class = MeasurementSerializer

    def perform_create(self, serializer):
        patient = Patient.objects.get(user=self.request.user)
        serializer.save(patient=patient)

    def get_queryset(self):
        q = Measurement.objects.filter(patient=Patient.objects.get(user=self.request.user))
        return q
