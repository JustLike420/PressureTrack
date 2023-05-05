from django.urls import path
from patients.views import MeasurementView, PatientView, DeviceView

urlpatterns = [
    path('patients/<int:user_id>/', PatientView.as_view({'get': 'list'}), name='create_patient'),
    path('patients/', PatientView.as_view({'post': 'create'}), name='patient'),

    path('measurements/<int:patient_id>/', MeasurementView.as_view({'get': 'list'}), name='get_measurements'),
    path('measurements/', MeasurementView.as_view({'post': 'create'}), name='measurements'),

    path('change_device/', DeviceView.as_view({'post': 'update'}), name='change_device'),
]
