from django.contrib.auth.models import User
from django.db import models


class Patient(models.Model):
    user = models.ForeignKey(
        'accounts.CustomUser',
        on_delete=models.CASCADE,
        related_name='patient',
        primary_key=True,
        unique=True
    )
    age = models.IntegerField()

    class Meta:
        db_table = 'patient'
