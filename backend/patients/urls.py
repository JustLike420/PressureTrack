from django.urls import path
from patients.views import MeasurementView, PatientView

urlpatterns = [
    path('patients/<int:user_id>/', PatientView.as_view({'get': 'list'}), name='create_patient'),
    path('patients/', PatientView.as_view({'post': 'create'}), name='patient'),

    path('measurements/<int:patient_id>/', MeasurementView.as_view({'get': 'list'}), name='get_measurements'),
    path('measurements/', MeasurementView.as_view({'post': 'create'}), name='measurements'),
]
