package com.pawcare.dto;

import java.time.LocalDateTime;

import com.pawcare.model.RequestStatus;

public record CorrectionResponse(
        Long id,
        Long providerId,
        String providerName,
        String field,
        String details,
        String reporterEmail,
        RequestStatus status,
        LocalDateTime createdAt) {
}
