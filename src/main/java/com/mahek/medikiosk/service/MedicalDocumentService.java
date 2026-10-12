
package com.mahek.medikiosk.service;

import com.mahek.medikiosk.entity.MedicalDocument;
import com.mahek.medikiosk.entity.Patient;
import com.mahek.medikiosk.repository.MedicalDocumentRepository;
import com.mahek.medikiosk.repository.PatientRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class MedicalDocumentService {

    private final MedicalDocumentRepository documentRepository;
    private final PatientRepository patientRepository;
    private final OcrService ocrService;

    @Value("${app.documents.storage-dir:uploads/medical-documents}")
    private String storageDirectory;

    public MedicalDocumentService(
            MedicalDocumentRepository documentRepository,
            PatientRepository patientRepository,
            OcrService ocrService) {

        this.documentRepository = documentRepository;
        this.patientRepository = patientRepository;
        this.ocrService = ocrService;
    }

    public MedicalDocument uploadDocument(
            Long patientId, MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please upload a file");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Patient not found"));

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Original filename is missing");
        }

        // Remove any directory information from the supplied filename.
        originalFileName = originalFileName.replace('\\', '/');
        originalFileName = originalFileName.substring(
                originalFileName.lastIndexOf('/') + 1);

        int dotIndex = originalFileName.lastIndexOf('.');

        if (dotIndex <= 0 || dotIndex == originalFileName.length() - 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "File must have a supported image extension");
        }

        String extension = originalFileName
                .substring(dotIndex + 1)
                .toLowerCase(Locale.ROOT);

        if (!extension.matches("png|jpg|jpeg|bmp|tif|tiff")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PNG, JPG, JPEG, BMP and TIFF images are supported");
        }

        String contentType = file.getContentType();

        if (contentType != null
                && !contentType.isBlank()
                && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Uploaded file must be an image");
        }

        Path storageRoot = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();

        Path savedFile = null;

        try {
            Files.createDirectories(storageRoot);

            // Generate a unique filename instead of trusting the upload name.
            String storedFileName = UUID.randomUUID() + "." + extension;

            savedFile = storageRoot.resolve(storedFileName).normalize();

            if (!savedFile.startsWith(storageRoot)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid file path");
            }

            Files.copy(
                    file.getInputStream(),
                    savedFile,
                    StandardCopyOption.REPLACE_EXISTING);

            // Extract text from the saved image using Tesseract.
            String extractedText = ocrService.extractText(savedFile);

            // Save the document details and OCR text in PostgreSQL.
            MedicalDocument document = new MedicalDocument();
            document.setPatient(patient);
            document.setOriginalFileName(originalFileName);
            document.setStoredFileName(storedFileName);
            document.setContentType(
                    contentType == null || contentType.isBlank()
                            ? "application/octet-stream"
                            : contentType);
            document.setStoragePath(savedFile.toString());
            document.setExtractedText(extractedText);

            return documentRepository.save(document);

        } catch (IOException e) {
            deleteFileQuietly(savedFile);

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not save the uploaded document",
                    e);

        } catch (RuntimeException e) {
            deleteFileQuietly(savedFile);
            throw e;
        }
    }

    private void deleteFileQuietly(Path file) {
        if (file != null) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) {
                // Preserve the original error if cleanup fails.
            }
        }
    }
}
