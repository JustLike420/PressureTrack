from rest_framework import permissions
from .models import Doctor, PatientDoctor
from patients.models import Patient


class IsDoctorsPatient(permissions.BasePermission):
    """Данный пациент привязан к доктору"""

    def has_permission(self, request, view):
        if request.data.get('patient') is not None:
            patient_id = request.data.get('patient')
            patient = Patient.objects.filter(pk=int(patient_id)).first()
            doctor = Doctor.objects.filter(user=request.user).first()
            status = PatientDoctor.objects.filter(doctor=doctor, patient=patient,
                                                  status=True).exists()
            return status
        elif request.query_params.get('patient') is not None:
            patient = Patient.objects.filter(pk=int(request.query_params.get('patient'))).first()
            doctor = Doctor.objects.filter(user=request.user).first()
            status = PatientDoctor.objects.filter(doctor=doctor, patient=patient,
                                                  status=True).exists()
            return status
        else:
            return False


class IsPatient(permissions.BasePermission):
    """Запись принадлежит пациенту"""

    def has_permission(self, request, view):
        patient = Patient.objects.filter(user=request.user).first()
        query_patient = request.query_params.get('patient')
        if query_patient is not None:
            return int(query_patient) == patient.pk
        else:
            return False
