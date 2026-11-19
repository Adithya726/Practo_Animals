package com.pawcare.ai;

import java.util.List;

import com.pawcare.dto.SlideDto;

/** What a writer hands back: the structured copy for one carousel, before it is saved. */
public record CarouselDraft(
        String title,
        List<SlideDto> slides,
        String caption,
        String hashtags,
        String generatedBy) {
}
