
package com.mahek.medikiosk.service;

import com.mahek.medikiosk.dto.ClinicalSummaryResponse;
import com.mahek.medikiosk.dto.ClinicalSummaryReviewRequest;
import com.mahek.medikiosk.entity.ClinicalSummary;
import com.mahek.medikiosk.entity.MedicalHistory;
import com.mahek.medikiosk.repository.ClinicalSummaryRepository;
import com.mahek.medikiosk.repository.MedicalHistoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ClinicalSummaryService {

    private final ClinicalSummaryRepository summaryRepository;
    private final MedicalHistoryRepository historyRepository;

    public ClinicalSummaryService(
            ClinicalSummaryRepository summaryRepository,
            MedicalHistoryRepository historyRepository) {

        this.summaryRepository = summaryRepository;
        this.historyRepository = historyRepository;
    }

    // Generate a draft clinical summary
    public ClinicalSummaryResponse generateSummary(Long historyId) {

        MedicalHistory history = historyRepository.findById(historyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Medical history not found"));

        ClinicalSummary summary = new ClinicalSummary();

        summary.setMedicalHistory(history);
        summary.setSummaryText(buildDraft(history));
        summary.setGenerationMethod("TEMPLATE_BASED_MVP");
        summary.setReviewStatus("PENDING_CLINICIAN_REVIEW");

        ClinicalSummary saved = summaryRepository.save(summary);

        return new ClinicalSummaryResponse(saved);
    }

    // Retrieve a summary by ID
    @Transactional(readOnly = true)
    public ClinicalSummaryResponse getSummary(Long id) {

        ClinicalSummary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Clinical summary not found"));

        return new ClinicalSummaryResponse(summary);
    }

    // Retrieve all summaries belonging to a medical history
    @Transactional(readOnly = true)
    public List<ClinicalSummaryResponse> getSummariesByHistory(
            Long historyId) {

        if (!historyRepository.existsById(historyId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Medical history not found");
        }

        return summaryRepository
                .findByMedicalHistory_IdOrderByCreatedAtDesc(historyId)
                .stream()
                .map(ClinicalSummaryResponse::new)
                .toList();
    }

    // Doctor reviews, edits, or approves a summary
    public ClinicalSummaryResponse reviewSummary(
            Long id,
            ClinicalSummaryReviewRequest request,
            String reviewer) {

        ClinicalSummary summary = summaryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Clinical summary not found"));

        // Update the text only when the doctor provides edited text
        if (request.getSummaryText() != null) {

            if (request.getSummaryText().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Summary text cannot be blank");
            }

            summary.setSummaryText(request.getSummaryText());
        }

        // Store the review decision
        summary.setReviewStatus(request.getReviewStatus());

        // Store reviewer information
        summary.setReviewedBy(reviewer);
        summary.setReviewedAt(LocalDateTime.now());

        ClinicalSummary saved = summaryRepository.save(summary);

        return new ClinicalSummaryResponse(saved);
    }

    // Build a structured draft from patient-reported information
    private String buildDraft(MedicalHistory history) {

        StringBuilder text = new StringBuilder();

        text.append("MEDIKIOSK - CLINICAL HISTORY SUMMARY\n");
        text.append("DRAFT FOR CLINICIAN REVIEW\n\n");

        text.append("Patient ID: ")
                .append(history.getPatient().getId())
                .append("\n");

        appendField(
                text,
                "Chief complaint",
                history.getChiefComplaint());

        appendField(
                text,
                "Symptoms",
                history.getSymptoms());

        appendField(
                text,
                "Duration",
                history.getDuration());

        appendField(
                text,
                "Past medical history",
                history.getPastMedicalHistory());

        appendField(
                text,
                "Current medications (patient-reported)",
                history.getCurrentMedications());

        appendField(
                text,
                "Allergies (patient-reported)",
                history.getAllergies());

        text.append("\nIMPORTANT: This summary organizes information ")
                .append("submitted during intake. Missing information ")
                .append("does not mean the condition or history is absent. ")
                .append("This is not a diagnosis or treatment recommendation. ")
                .append("A qualified clinician must verify the details ")
                .append("before relying on this summary.");

        return text.toString();
    }

    private void appendField(
            StringBuilder text,
            String label,
            String value) {

        text.append(label)
                .append(": ")
                .append(value == null || value.isBlank()
                        ? "Not provided"
                        : value)
                .append("\n");
    }
}
