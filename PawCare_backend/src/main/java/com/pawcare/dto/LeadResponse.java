package com.pawcare.dto;

import java.time.LocalDateTime;

import com.pawcare.model.LeadStatus;
import com.pawcare.model.LeadType;

public record LeadResponse(
        Long id,
        String name,
        String email,
        String phone,
        String background,
        Long courseId,
        String courseTitle,
        LeadType type,
        String message,
        String source,
        LeadStatus status,
        LocalDateTime createdAt) {
}
