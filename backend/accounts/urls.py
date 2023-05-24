from django.urls import path, include

from accounts.views import CreatePatientView, ProfileView

urlpatterns = [
    path(r'auth/', include('djoser.urls.authtoken')),
    path('craete_patient/', CreatePatientView.as_view({'post': 'create'})),
    path('profile/<int:pk>', ProfileView.as_view({'get': 'retrieve'}))
]
