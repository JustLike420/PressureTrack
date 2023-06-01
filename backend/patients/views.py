from drf_yasg import openapi
from drf_yasg.utils import swagger_auto_schema
from rest_framework import viewsets, status
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework.views import APIView

from doctors.models import Treatment
from doctors.serializers import TreatmentListSerializer
from patients.models import Measurement, Patient
from patients.serializers import MeasurementSerializer, PatientSerializer, DeviceSerializer
from rest_framework.exceptions import NotAuthenticated, server_error, NotFound


class PatientView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        responses={
            200: openapi.Response('Success', PatientSerializer),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def get(self, request):
        queryset = Patient.objects.get(user=self.request.user)
        serializer = PatientSerializer(queryset)
        return Response(serializer.data)


class DeviceView(APIView):
    permission_classes = [IsAuthenticated]

    @swagger_auto_schema(
        request_body=DeviceSerializer,
        responses={
            200: openapi.Response('Success'),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def put(self, request):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            patient = Patient.objects.get(user=self.request.user)
            device = request.data.get('device')
            patient.device = device
            patient.save()
            # serializer = PatientSerializer(patient)
            # return Response(serializer.data, status=status.HTTP_200_OK)
            return Response(status=status.HTTP_200_OK)
        except Exception as e:
            return server_error(request)


class MeasurementsView(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Measurement.objects.all()
    serializer_class = MeasurementSerializer

    @swagger_auto_schema(
        responses={
            201: openapi.Response('Created'),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def create(self, request, *args, **kwargs):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            patient = Patient.objects.get(user=request.user)
            serializer = self.get_serializer(data=request.data)
            serializer.is_valid(raise_exception=True)
            serializer.save(patient=patient)
            headers = self.get_success_headers(serializer.data)
            return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)
        except Exception as e:
            return server_error(request)

    @swagger_auto_schema(
        responses={
            200: openapi.Response('Success', MeasurementSerializer),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def list(self, request, *args, **kwargs):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            queryset = self.filter_queryset(self.get_queryset())
            serializer = self.get_serializer(queryset, many=True)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except Exception as e:
            return server_error(request)


class TreatmentsView(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Treatment.objects.all()
    serializer_class = TreatmentListSerializer

    @swagger_auto_schema(
        responses={
            200: openapi.Response('Success', TreatmentListSerializer),
            401: openapi.Response('Unauthorized'),
            500: openapi.Response('Internal Server Error'),
        }
    )
    def list(self, request):
        if not request.user.is_authenticated:
            raise NotAuthenticated()
        try:
            return Treatment.objects.filter(patient=Patient.objects.get(user=self.request.user))
        except Exception as e:
            return server_error(request)
