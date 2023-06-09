from rest_framework import serializers
from accounts.serializers import CustomUserSerializer
from patients.models import Patient, Measurement


class PatientSerializer(serializers.ModelSerializer):
    user = CustomUserSerializer()

    class Meta:
        model = Patient
        fields = "__all__"


class MeasurementSerializer(serializers.ModelSerializer):
    class Meta:
        model = Measurement
        exclude = ('patient',)


class DeviceSerializer(serializers.Serializer):
    device = serializers.CharField(max_length=200)
