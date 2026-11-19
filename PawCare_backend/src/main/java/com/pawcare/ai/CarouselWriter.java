package com.pawcare.ai;

import com.pawcare.dto.CarouselGenerateRequest;

/**
 * Steps 2 and 3 of the content workflow: research and structure the idea, then
 * write the headline and slide-by-slide copy with art direction for each slide.
 */
public interface CarouselWriter {

    CarouselDraft write(CarouselGenerateRequest request);

    /** Whether this writer can run right now (for example, an API key is configured). */
    boolean isAvailable();
}
