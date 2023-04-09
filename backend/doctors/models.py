from django.contrib.auth.models import User
from django.db import models


class Doctor(models.Model):
    user = models.ForeignKey(
        'accounts.CustomUser',
        on_delete=models.CASCADE,
        related_name='doctor',
        primary_key=True,
        unique=True
    )


    class Meta:
        db_table = 'doctor'


class PatientDoctor(models.Model):
    doctor = models.OneToOneField(Doctor, on_delete=models.CASCADE, related_name='patient_doctor')
    patient = models.OneToOneField(
        'patients.Patient',
        on_delete=models.CASCADE,
        related_name='doctors_patient'
    )
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    status = models.BooleanField(default=True)

    class Meta:
        db_table = 'patient_doctor'
        constraints = [
            models.UniqueConstraint(
                fields=['doctor', 'patient'],
                name='unique_patient_doctor'
            )
        ]


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
