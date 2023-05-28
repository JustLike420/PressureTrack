from django.urls import path, include
from rest_framework import routers

from .views import PatientTreatmentsView, PatientDoctorStatusView, ShowPatientsView, ShowDetailView

urlpatterns = [
    path('patients/', ShowPatientsView.as_view()),
    path('patients/<int:pk>', ShowDetailView.as_view()),
    path('treatments/<int:pk>', PatientTreatmentsView.as_view()),
    path('change_status/<int:pk>', PatientDoctorStatusView.as_view(), name='archive'),
]
