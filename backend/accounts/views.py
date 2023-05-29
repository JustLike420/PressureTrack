from django.shortcuts import render

# Create your views here.
from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from accounts.models import CustomUser
from accounts.serializers import CreatePatient, CustomUserSerializer
from patients.models import Patient


class CreatePatientView(viewsets.ModelViewSet):
    queryset = Patient.objects.all()
    serializer_class = CreatePatient


# class ProfileView(viewsets.ModelViewSet):
#     serializer_class = CustomUserSerializer
#     queryset = CustomUser.objects.all()
class ProfileView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(responses={
        200: openapi.Response('response description', CustomUserSerializer)
    })
    def get(self, request):
        serializer = CustomUserSerializer(self.request.user)
        return Response(serializer.data)
