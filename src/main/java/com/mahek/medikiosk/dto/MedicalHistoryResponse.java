
package com.mahek.medikiosk.dto;

import com.mahek.medikiosk.entity.MedicalHistory;
import java.time.LocalDateTime;

public class MedicalHistoryResponse {

    private final Long id;
    private final Long patientId;
    private final String chiefComplaint;
    private final String symptoms;
    private final String duration;
    private final String pastMedicalHistory;
    private final String currentMedications;
    private final String allergies;
    private final LocalDateTime createdAt;

    public MedicalHistoryResponse(MedicalHistory history) {
        this.id = history.getId();
        this.patientId = history.getPatient().getId();
        this.chiefComplaint = history.getChiefComplaint();
        this.symptoms = history.getSymptoms();
        this.duration = history.getDuration();
        this.pastMedicalHistory = history.getPastMedicalHistory();
        this.currentMedications = history.getCurrentMedications();
        this.allergies = history.getAllergies();
        this.createdAt = history.getCreatedAt();
    }

    public Long getId() { return id; }
    public Long getPatientId() { return patientId; }
    public String getChiefComplaint() { return chiefComplaint; }
    public String getSymptoms() { return symptoms; }
    public String getDuration() { return duration; }
    public String getPastMedicalHistory() { return pastMedicalHistory; }
    public String getCurrentMedications() { return currentMedications; }
    public String getAllergies() { return allergies; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
