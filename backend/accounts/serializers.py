from django.db import transaction
from djoser.serializers import UserCreateSerializer
from rest_framework import serializers
from djoser.conf import settings
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





class TokenSerializer(serializers.ModelSerializer):
    auth_token = serializers.CharField(source="key")
    user_role = serializers.SerializerMethodField(read_only=True)

    class Meta:
        model = settings.TOKEN_MODEL
        fields = ("auth_token", "user_role")

    def get_user_role(self, obj):
        if Patient.objects.filter(user=obj.user) is not None:
            return "patient"
        elif Doctor.objects.filter(user=obj.user) is not None:
            return "doctor"
        else:
            return "undefined"
