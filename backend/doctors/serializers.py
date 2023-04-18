from rest_framework import serializers
from .models import Treatment


class TreatmentSerializer(serializers.ModelSerializer):
    created_at = serializers.DateTimeField(format='%d.%m.%Y', read_only=True)

    class Meta:
        model = Treatment
        fields = "__all__"
