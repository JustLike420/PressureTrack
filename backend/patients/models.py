from django.contrib.auth.models import User
from django.db import models


class Patient(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    age = models.IntegerField()

    class Meta:
        db_table = 'patient'
