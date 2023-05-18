from django.urls import path
from patients.views import PatientView, DeviceView, MeasurementsView

urlpatterns = [
    path('profile/', PatientView.as_view()),
    path('change_device/', DeviceView.as_view(), name='change_device'),
    path('measurements/', MeasurementsView.as_view({'get': 'list', 'post': 'create'}))
]
