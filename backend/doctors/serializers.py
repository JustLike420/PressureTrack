from rest_framework import serializers
from accounts.serializers import CustomUserSerializer
from patients.models import Patient
from .models import Treatment


class TreatmentListSerializer(serializers.ModelSerializer):
    created_at = serializers.DateTimeField(format='%d.%m.%Y', read_only=True)

    class Meta:
        model = Treatment
        fields = ("message", "created_at")


class TreatmentCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = Treatment
        fields = ('message',)


class DoctorPatientListSerializer(serializers.ModelSerializer):
    user = CustomUserSerializer()

    class Meta:
        model = Patient
        fields = ("pk", "user", "treatment_start")


class DoctorPatientDetailSerializer(serializers.ModelSerializer):
    user = CustomUserSerializer()

    class Meta:
        model = Patient
        fields = ('user', 'height', 'weight', 'device', 'treatment_start')
