
package com.mahek.medikiosk.service;

import com.mahek.medikiosk.dto.MedicalHistoryRequest;
import com.mahek.medikiosk.dto.MedicalHistoryResponse;
import com.mahek.medikiosk.entity.MedicalHistory;
import com.mahek.medikiosk.entity.Patient;
import com.mahek.medikiosk.repository.MedicalHistoryRepository;
import com.mahek.medikiosk.repository.PatientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class MedicalHistoryService {

    private final MedicalHistoryRepository historyRepository;
    private final PatientRepository patientRepository;

    public MedicalHistoryService(
            MedicalHistoryRepository historyRepository,
            PatientRepository patientRepository) {
        this.historyRepository = historyRepository;
        this.patientRepository = patientRepository;
    }

    public MedicalHistoryResponse createHistory(
            MedicalHistoryRequest request) {

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Patient not found"));

        MedicalHistory history = new MedicalHistory();
        history.setPatient(patient);
        history.setChiefComplaint(request.getChiefComplaint());
        history.setSymptoms(request.getSymptoms());
        history.setDuration(request.getDuration());
        history.setPastMedicalHistory(request.getPastMedicalHistory());
        history.setCurrentMedications(request.getCurrentMedications());
        history.setAllergies(request.getAllergies());

        MedicalHistory saved = historyRepository.save(history);

        return new MedicalHistoryResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MedicalHistoryResponse> getHistoryByPatient(
            Long patientId) {

        if (!patientRepository.existsById(patientId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Patient not found");
        }

        return historyRepository
                .findByPatient_IdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(MedicalHistoryResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public MedicalHistoryResponse getHistoryById(Long id) {

        MedicalHistory history = historyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Medical history not found"));

        return new MedicalHistoryResponse(history);
    }
}

