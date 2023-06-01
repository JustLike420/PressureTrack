from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView
from accounts.serializers import CreatePatient, CustomUserSerializer
from patients.models import Patient
from rest_framework.exceptions import NotAuthenticated, server_error, NotFound


class CreatePatientView(viewsets.ModelViewSet):
    queryset = Patient.objects.all()
    serializer_class = CreatePatient


class ProfileView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        responses={
            200: openapi.Response('response description', CustomUserSerializer),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def get(self, request):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            serializer = CustomUserSerializer(self.request.user)
            return Response(serializer.data)
        except Exception as e:
            return server_error(request)
