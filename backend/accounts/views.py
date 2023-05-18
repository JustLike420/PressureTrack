from django.shortcuts import render

# Create your views here.
from rest_framework import viewsets

from accounts.models import CustomUser
from accounts.serializers import CreatePatient, CustomUserSerializer
from patients.models import Patient


class CreatePatientView(viewsets.ModelViewSet):
    queryset = Patient.objects.all()
    serializer_class = CreatePatient


class ProfileView(viewsets.ModelViewSet):
    serializer_class = CustomUserSerializer
    queryset = CustomUser.objects.all()
