from django.contrib.auth.models import User
from django.db import models
from admins.models import BaseUser


class Patient(BaseUser):
    class Meta:
        db_table = 'patient'
