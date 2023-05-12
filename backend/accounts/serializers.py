from django.db import transaction
from djoser.serializers import UserCreateSerializer
from rest_framework import serializers, status
from rest_framework.response import Response

from .models import CustomUser
from patients.models import Patient
from doctors.models import Doctor, PatientDoctor


class CreatePatient(serializers.ModelSerializer):
    user = UserCreateSerializer()
    doctor = serializers.CharField(write_only=True)

    class Meta:
        model = Patient
        fields = '__all__'

    @transaction.atomic
    def create(self, validation_data, *args, **kwargs):
        user = validation_data.pop('user')
        snils = validation_data.pop('doctor')
        patient_user = CustomUser.objects.create(**user)
        patient = Patient.objects.create(user=patient_user, **validation_data)
        try:
            doctor = Doctor.objects.get(user__snils=snils)
        except Doctor.DoesNotExist:
            raise serializers.ValidationError({"error": "Doctor does not exist."})

        PatientDoctor.objects.create(doctor=doctor, patient=patient, status=True)
        return patient


class CustomUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = CustomUser
        fields = ('first_name', 'last_name', 'phone')
