package com.mahek.medikiosk.repository;

import com.mahek.medikiosk.entity.ClinicalSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClinicalSummaryRepository
        extends JpaRepository<ClinicalSummary, Long> {

    List<ClinicalSummary>
    findByMedicalHistory_IdOrderByCreatedAtDesc(Long historyId);
}