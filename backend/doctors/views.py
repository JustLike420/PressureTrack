from django.shortcuts import render
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework import viewsets
from .models import Treatment
from .serializers import TreatmentSerializer


class TreatmentView(viewsets.ModelViewSet):
    serializer_class = TreatmentSerializer
    queryset = Treatment.objects.all()
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['patient']

# Create your views here.
