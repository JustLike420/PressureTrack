from django.urls import path
from .views import TreatmentView, PatientDoctorStatusView

urlpatterns = [
    path('treatments/', TreatmentView.as_view({
        'get': 'list',
        'post': 'create'
    }), name='treatments'),
    path('archive/<int:pk>', PatientDoctorStatusView.as_view(), name='archive')
]
