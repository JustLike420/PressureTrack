from django.contrib.auth import get_user_model
from django.core.management.base import BaseCommand

User = get_user_model()


class Command(BaseCommand):
    def handle(self, *args, **options):
        if not User.objects.filter(email="admin@mail.ru"):
            print("Creating admin account...")
            User.objects.create_superuser(
                email="admin@mail.ru",
                first_name="admin",
                last_name="admin",
                password="123",
            )
        else:
            print("Admin already initialized")
