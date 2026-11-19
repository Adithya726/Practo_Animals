package com.pawcare.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CarouselResponse(
        Long id,
        String idea,
        String title,
        String platform,
        String tone,
        String theme,
        String caption,
        String hashtags,
        String generatedBy,
        List<SlideDto> slides,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
