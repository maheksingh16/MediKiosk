package com.mahek.medikiosk.service;

import com.mahek.medikiosk.entity.Patient;
import com.mahek.medikiosk.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // Register a patient
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Get a patient by ID
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    // Update an existing patient
    public Optional<Patient> updatePatient(
            Long id, Patient updatedPatient) {

        return patientRepository.findById(id).map(existingPatient -> {

            existingPatient.setFirstName(updatedPatient.getFirstName());
            existingPatient.setLastName(updatedPatient.getLastName());
            existingPatient.setDateOfBirth(updatedPatient.getDateOfBirth());
            existingPatient.setGender(updatedPatient.getGender());
            existingPatient.setPhoneNumber(updatedPatient.getPhoneNumber());

            return patientRepository.save(existingPatient);
        });
    }

    // Delete a patient
    public boolean deletePatient(Long id) {

        if (!patientRepository.existsById(id)) {
            return false;
        }

        patientRepository.deleteById(id);
        return true;
    }
}