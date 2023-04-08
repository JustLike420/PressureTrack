from django.contrib.auth.models import User
from django.db import models
from admins.models import BaseUser


class Doctor(BaseUser):
    class Meta:
        db_table = 'doctor'


class PatientDoctor(models.Model):
    doctor = models.OneToOneField(
        Doctor,
        on_delete=models.CASCADE,
        related_name='patient_doctor'
    )
    patient = models.OneToOneField(
        'patients.Patient',
        on_delete=models.CASCADE,
        related_name='doctors_patient'
    )
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    status = models.BooleanField(default=True)


class Treatment(models.Model):
    patient = models.OneToOneField(
        'patients.Patient',
        on_delete=models.CASCADE,
        related_name='patient_treatment',
        default=1
    )
    message = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'treatment'
