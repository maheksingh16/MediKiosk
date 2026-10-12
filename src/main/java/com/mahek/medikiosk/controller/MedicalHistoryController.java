
package com.mahek.medikiosk.controller;

import com.mahek.medikiosk.dto.MedicalHistoryRequest;
import com.mahek.medikiosk.dto.MedicalHistoryResponse;
import com.mahek.medikiosk.service.MedicalHistoryService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-histories")
public class MedicalHistoryController {

    private final MedicalHistoryService historyService;

    public MedicalHistoryController(
            MedicalHistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping
    public ResponseEntity<MedicalHistoryResponse> createHistory(
            @Valid @RequestBody MedicalHistoryRequest request) {

        MedicalHistoryResponse response =
                historyService.createHistory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/patient/{patientId}")
    public List<MedicalHistoryResponse> getHistoryByPatient(
            @PathVariable Long patientId) {

        return historyService.getHistoryByPatient(patientId);
    }

    @GetMapping("/{id}")
    public MedicalHistoryResponse getHistoryById(
            @PathVariable Long id) {

        return historyService.getHistoryById(id);
    }
}
