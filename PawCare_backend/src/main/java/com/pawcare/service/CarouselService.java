package com.pawcare.service;

import java.util.List;

import com.pawcare.dto.CarouselGenerateRequest;
import com.pawcare.dto.CarouselResponse;
import com.pawcare.dto.CarouselUpdateRequest;

public interface CarouselService {

    CarouselResponse generate(CarouselGenerateRequest request);

    CarouselResponse getById(Long id);

    CarouselResponse update(Long id, CarouselUpdateRequest request);

    List<CarouselResponse> recent();

    void delete(Long id);

    /** Which writer new carousels use: the AI model name, or TEMPLATE. */
    String activeWriter();
}
