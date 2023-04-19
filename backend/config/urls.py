from django.contrib import admin
from django.urls import path, include
from .yasg import urlpatterns as doc_urls

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/v1/', include('doctors.urls')),
    path('api/v1/', include('patients.urls')),
]
urlpatterns += doc_urls
