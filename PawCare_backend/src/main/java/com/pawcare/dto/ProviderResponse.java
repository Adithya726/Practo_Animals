package com.pawcare.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.pawcare.model.ProviderCategory;

public record ProviderResponse(
        Long id,
        String name,
        ProviderCategory category,
        String categoryLabel,
        String city,
        String area,
        String address,
        String phone,
        String about,
        String openingHours,
        int feeFrom,
        double rating,
        int reviewCount,
        int yearsActive,
        boolean open24x7,
        boolean emergency,
        boolean homeVisit,
        boolean verified,
        List<String> species,
        List<String> services,
        LocalDateTime updatedAt) {
}
