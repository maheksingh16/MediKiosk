
package com.mahek.medikiosk.repository;

import com.mahek.medikiosk.entity.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalDocumentRepository
        extends JpaRepository<MedicalDocument, Long> {

    List<MedicalDocument> findByPatient_IdOrderByUploadedAtDesc(
            Long patientId
    );
}
