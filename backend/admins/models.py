from django.contrib.auth.models import User
from django.db import models


class BaseUser(models.Model):
    id = models.AutoField(User, primary_key=True)
    snils = models.CharField(max_length=14, unique=True)
    first_name = models.TextField()
    second_name = models.TextField()
    last_name = models.TextField(null=True)
    age = models.IntegerField()
    password = models.BinaryField()

    class Meta:
        abstract = True
