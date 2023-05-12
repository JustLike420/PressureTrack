from django.shortcuts import render

# Create your views here.
from rest_framework import viewsets

from accounts.serializers import CreatePatient
from patients.models import Patient


class CreatePatientView(viewsets.ModelViewSet):
    queryset = Patient.objects.all()
    serializer_class = CreatePatient
