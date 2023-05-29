from django.urls import path, include

from accounts.views import CreatePatientView, ProfileView

urlpatterns = [
    path(r'auth/', include('djoser.urls.authtoken')),
    path('craete_patient/', CreatePatientView.as_view({'post': 'create'})),
    path('profile/', ProfileView.as_view())
]
