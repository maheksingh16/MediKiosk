
package com.mahek.medikiosk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Service
public class OcrService {

    @Value("${app.ocr.tesseract-path}")
    private String tesseractPath;

    @Value("${app.ocr.tessdata-path}")
    private String tessdataPath;

    @Value("${app.ocr.language:eng}")
    private String language;

    public String extractText(Path imagePath) {

        // Check whether the image exists
        if (imagePath == null || !Files.isRegularFile(imagePath)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Image file not found");
        }

        Path outputFile = null;

        try {
            // Temporary file to capture Tesseract's extracted text
            outputFile = Files.createTempFile(
                    "medikiosk-ocr-", ".txt");

            // Build the Tesseract command
            ProcessBuilder builder = new ProcessBuilder(
                    tesseractPath,
                    imagePath.toAbsolutePath().toString(),
                    "stdout",
                    "-l", language,
                    "--tessdata-dir", tessdataPath
            );

            // Write OCR output to a temporary file
            builder.redirectOutput(outputFile.toFile());

            // Keep error messages separate from extracted text
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = builder.start();

            // Wait up to 60 seconds for OCR processing
            boolean completed = process.waitFor(
                    60, TimeUnit.SECONDS);

            if (!completed) {
                process.destroyForcibly();
                process.waitFor();

                throw new ResponseStatusException(
                        HttpStatus.REQUEST_TIMEOUT,
                        "OCR processing took too long");
            }

            // Verify that Tesseract completed successfully
            if (process.exitValue() != 0) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Tesseract could not process the image");
            }

            // Read the actual extracted text
            return Files.readString(
                    outputFile,
                    StandardCharsets.UTF_8
            ).trim();

        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Could not run Tesseract or read OCR output",
                    e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "OCR processing was interrupted",
                    e);

        } finally {
            // Remove temporary OCR output after processing
            if (outputFile != null) {
                try {
                    Files.deleteIfExists(outputFile);
                } catch (IOException ignored) {
                    // Cleanup failure does not replace the main result.
                }
            }
        }
    }
}
