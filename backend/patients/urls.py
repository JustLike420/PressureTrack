from django.urls import path
from patients.views import PatientView, DeviceView, MeasurementsView, TreatmentsView

urlpatterns = [
    path('profile/', PatientView.as_view()),
    path('change_device/', DeviceView.as_view(), name='change_device'),
    path('measurements/', MeasurementsView.as_view({'get': 'list', 'post': 'create'})),
    path('treatments/', TreatmentsView.as_view({'get': 'list'})),
]
