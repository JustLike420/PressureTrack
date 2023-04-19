from django_filters.rest_framework import DjangoFilterBackend
from rest_framework import viewsets
from patients.models import Measurement, Patient
from patients.serializers import MeasurementSerializer, PatientSerializer
from datetime import datetime, timedelta
from django.db.models import Q


class PatientView(viewsets.ModelViewSet):
    serializer_class = PatientSerializer
    queryset = Patient.objects.all()
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['user_id']

    def get_queryset(self):
        queryset = self.queryset.filter(user_id=self.kwargs['user_id'])
        return queryset


class MeasurementView(viewsets.ModelViewSet):
    serializer_class = MeasurementSerializer
    # filter for one week

    queryset = Measurement.objects.all()
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['created_at', 'patient_id']

    def get_queryset(self):
        week_ago = datetime.now() - timedelta(days=7)
        queryset = self.queryset.filter(Q(created_at__gte=week_ago), patient_id=self.kwargs['patient_id'])
        return queryset
