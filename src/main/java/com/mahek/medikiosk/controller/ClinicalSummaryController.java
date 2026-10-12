
package com.mahek.medikiosk.controller;

import com.mahek.medikiosk.dto.ClinicalSummaryResponse;
import com.mahek.medikiosk.dto.ClinicalSummaryReviewRequest;
import com.mahek.medikiosk.service.ClinicalSummaryService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinical-summaries")
public class ClinicalSummaryController {

    private final ClinicalSummaryService summaryService;

    public ClinicalSummaryController(
            ClinicalSummaryService summaryService) {
        this.summaryService = summaryService;
    }

    // POST: Generate a clinical summary
    @PostMapping("/history/{historyId}/generate")
    public ResponseEntity<ClinicalSummaryResponse> generateSummary(
            @PathVariable Long historyId) {

        ClinicalSummaryResponse response =
                summaryService.generateSummary(historyId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET: Get a summary by ID
    @GetMapping("/{id}")
    public ClinicalSummaryResponse getSummary(
            @PathVariable Long id) {

        return summaryService.getSummary(id);
    }

    // GET: Get all summaries for a medical history
    @GetMapping("/history/{historyId}")
    public List<ClinicalSummaryResponse> getSummariesByHistory(
            @PathVariable Long historyId) {

        return summaryService.getSummariesByHistory(historyId);
    }

    // PUT: Doctor reviews a summary
    @PutMapping("/{id}/review")
    public ResponseEntity<ClinicalSummaryResponse> reviewSummary(
            @PathVariable Long id,
            @Valid @RequestBody ClinicalSummaryReviewRequest request,
            Authentication authentication) {

        ClinicalSummaryResponse response =
                summaryService.reviewSummary(
                        id,
                        request,
                        authentication.getName());

        return ResponseEntity.ok(response);
    }
}
