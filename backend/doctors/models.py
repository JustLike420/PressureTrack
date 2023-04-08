from django.contrib.auth.models import User
from django.db import models


class Doctor(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    snils = models.CharField(max_length=14, unique=True)

    class Meta:
        db_table = 'doctor'


class PatientDoctor(models.Model):
    doctor = models.ForeignKey(Doctor, on_delete=models.CASCADE, related_name='patient_doctor')
    patient = models.ForeignKey(
        'patients.Patient',
        on_delete=models.CASCADE,
        related_name='doctors_patient'
    )
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    status = models.BooleanField(default=True)


class Treatment(models.Model):
    patient = models.ForeignKey(
        'patients.Patient',
        on_delete=models.CASCADE,
        related_name='patient_treatment',
        default=1
    )
    message = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'treatment'
