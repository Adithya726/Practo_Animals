package com.pawcare.dto;

import java.time.LocalDateTime;

import com.pawcare.model.ProviderCategory;
import com.pawcare.model.RequestStatus;

public record ListingResponse(
        Long id,
        String businessName,
        ProviderCategory category,
        String categoryLabel,
        String city,
        String area,
        String address,
        String contactName,
        String phone,
        String email,
        String about,
        int feeFrom,
        boolean open24x7,
        boolean homeVisit,
        RequestStatus status,
        Long providerId,
        LocalDateTime createdAt) {
}
