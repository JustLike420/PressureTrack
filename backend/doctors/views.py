from django.shortcuts import render
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework import viewsets, status
from rest_framework.views import APIView
from patients.models import Patient
from .models import Treatment, PatientDoctor, Doctor
from .serializers import TreatmentSerializer
from rest_framework.response import Response


class TreatmentView(viewsets.ModelViewSet):
    serializer_class = TreatmentSerializer
    queryset = Treatment.objects.all()
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['patient']


class PatientDoctorStatusView(APIView):
    def put(self, request, pk):
        if (patient := Patient.objects.filter(pk=pk)) is None:
            return Response(data={'model': 'patient'}, status=status.HTTP_404_NOT_FOUND)
        if (doctor := Doctor.objects.filter(user=request.user)) is None:
            return Response(data={'model': 'doctor'}, status=status.HTTP_404_NOT_FOUND)

        try:
            relation = PatientDoctor.objects.get(patient=patient, doctor=doctor)
            relation.status = False
            relation.save()
            return Response({"status": "Status changed"})
        except PatientDoctor.DoesNotExist:
            return Response(data={'model': 'pd'}, status=status.HTTP_404_NOT_FOUND)
