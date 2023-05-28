from django.contrib import admin
from django.urls import path, include
from .yasg import urlpatterns as doc_urls

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/v1/doctor/', include('doctors.urls')),
    path('api/v1/patient/', include('patients.urls')),
    path('api/v1/accounts/', include('accounts.urls')),
]
urlpatterns += doc_urls
