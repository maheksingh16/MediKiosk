package com.mahek.medikiosk.dto;

import com.mahek.medikiosk.entity.ClinicalSummary;
import java.time.LocalDateTime;

public class ClinicalSummaryResponse {

    private final Long id;
    private final Long patientId;
    private final Long medicalHistoryId;
    private final String summaryText;
    private final String generationMethod;
    private final String reviewStatus;
    private final LocalDateTime createdAt;

    public ClinicalSummaryResponse(ClinicalSummary summary) {
        this.id = summary.getId();
        this.patientId =
                summary.getMedicalHistory().getPatient().getId();
        this.medicalHistoryId =
                summary.getMedicalHistory().getId();
        this.summaryText = summary.getSummaryText();
        this.generationMethod = summary.getGenerationMethod();
        this.reviewStatus = summary.getReviewStatus();
        this.createdAt = summary.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public Long getMedicalHistoryId() {
        return medicalHistoryId;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public String getGenerationMethod() {
        return generationMethod;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}