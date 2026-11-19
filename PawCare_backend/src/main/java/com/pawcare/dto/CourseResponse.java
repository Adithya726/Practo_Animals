package com.pawcare.dto;

import java.time.LocalDate;
import java.util.List;

public record CourseResponse(
        Long id,
        String slug,
        String title,
        String tagline,
        String level,
        String audience,
        int durationWeeks,
        int hoursPerWeek,
        String mode,
        int priceInr,
        LocalDate startDate,
        int seatsTotal,
        int seatsLeft,
        List<String> outcomes,
        List<String> tools) {
}
