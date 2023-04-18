from django.contrib.auth.models import User
from django.db import models
from doctors.models import Treatment


class Patient(models.Model):
    user = models.OneToOneField(
        'accounts.CustomUser',
        on_delete=models.CASCADE,
        related_name='patient',
    )
    age = models.IntegerField()
    height = models.FloatField()
    weight = models.FloatField()
    device = models.TextField()

    class Meta:
        db_table = 'patient'


class Measurement(models.Model):
    patient = models.ForeignKey(
        'patients.Patient',
        on_delete=models.CASCADE,
        related_name='measurements',
    )
    top = models.IntegerField()
    bottom = models.IntegerField()
    pulse = models.IntegerField()
    comment = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        db_table = 'measurement'
