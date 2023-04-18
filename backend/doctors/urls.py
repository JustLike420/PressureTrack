from django.urls import path
from .views import TreatmentView

urlpatterns = [
    path('treatments/', TreatmentView.as_view({'get': 'list', 'post': 'create'}), name='treatments')
]
