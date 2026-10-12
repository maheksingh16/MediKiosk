
package com.mahek.medikiosk.controller;

import com.mahek.medikiosk.entity.MedicalDocument;
import com.mahek.medikiosk.repository.MedicalDocumentRepository;
import com.mahek.medikiosk.service.MedicalDocumentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medical-documents")
public class MedicalDocumentController {

    private final MedicalDocumentService documentService;
    private final MedicalDocumentRepository documentRepository;

    public MedicalDocumentController(
            MedicalDocumentService documentService,
            MedicalDocumentRepository documentRepository) {

        this.documentService = documentService;
        this.documentRepository = documentRepository;
    }

    // Upload a medical image and extract its text.
    @PostMapping(
            value = "/patient/{patientId}/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Map<String, Object>> uploadDocument(
            @PathVariable Long patientId,
            @RequestParam("file") MultipartFile file) {

        MedicalDocument savedDocument =
                documentService.uploadDocument(patientId, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(savedDocument));
    }

    // Get one document and its extracted OCR text.
    @GetMapping("/{documentId}")
    public ResponseEntity<Map<String, Object>> getDocument(
            @PathVariable Long documentId) {

        MedicalDocument document = documentRepository
                .findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Medical document not found"));

        return ResponseEntity.ok(toResponse(document));
    }

    // Get all documents uploaded for a patient.
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Map<String, Object>>> getPatientDocuments(
            @PathVariable Long patientId) {

        List<MedicalDocument> documents =
                documentRepository
                        .findByPatient_IdOrderByUploadedAtDesc(patientId);

        List<Map<String, Object>> response = new ArrayList<>();

        for (MedicalDocument document : documents) {
            response.add(toResponse(document));
        }

        return ResponseEntity.ok(response);
    }

    // Return only the information the API needs to expose.
    private Map<String, Object> toResponse(MedicalDocument document) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("id", document.getId());
        response.put("patientId", document.getPatient().getId());
        response.put("originalFileName",
                document.getOriginalFileName());
        response.put("contentType", document.getContentType());
        response.put("extractedText", document.getExtractedText());
        response.put("uploadedAt", document.getUploadedAt());

        return response;
    }
}
