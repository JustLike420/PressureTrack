from django.contrib.auth import get_user_model
from django.core.management.base import BaseCommand

from accounts.models import CustomUser

User = get_user_model()


class Command(BaseCommand):
    def handle(self, *args, **options):
        if not CustomUser.objects.filter(email="admin@mail.ru"):
            print("Creating admin account...")
            CustomUser.objects.create_superuser(
                email="admin@mail.ru",
                first_name="admin",
                last_name="admin",
                password="123",
                is_staff=True
            )
        else:
            print("Admin already initialized")
