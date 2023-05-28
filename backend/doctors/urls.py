from django.urls import path

from .views import (
    PatientTreatmentsView,
    PatientDoctorStatusView,
    ShowPatientsView,
    ShowDetailView,
    ShowMeasurementsView
)

urlpatterns = [
    path('patients/', ShowPatientsView.as_view()),
    path('patients/<int:pk>', ShowDetailView.as_view()),
    path('treatments/<int:pk>', PatientTreatmentsView.as_view()),
    path('change_status/<int:pk>', PatientDoctorStatusView.as_view()),
    path('measurements/<int:pk>', ShowMeasurementsView.as_view())
]
