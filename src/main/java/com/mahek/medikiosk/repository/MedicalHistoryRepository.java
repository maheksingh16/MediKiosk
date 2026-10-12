
package com.mahek.medikiosk.repository;

import com.mahek.medikiosk.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalHistoryRepository
        extends JpaRepository<MedicalHistory, Long> {

    List<MedicalHistory> findByPatient_IdOrderByCreatedAtDesc(
            Long patientId);
}
