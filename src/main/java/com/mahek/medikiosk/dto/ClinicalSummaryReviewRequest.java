package com.mahek.medikiosk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ClinicalSummaryReviewRequest {

    @NotBlank(message = "Review status is required")
    @Pattern(
            regexp = "APPROVED|NEEDS_REVISION",
            message = "Status must be APPROVED or NEEDS_REVISION"
    )
    private String reviewStatus;

    @Size(max = 10000, message = "Summary is too long")
    private String summaryText;

    public ClinicalSummaryReviewRequest() {
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setSummaryText(String summaryText) {
        this.summaryText = summaryText;
    }
}