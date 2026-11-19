package com.pawcare.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawcare.ai.CarouselDraft;
import com.pawcare.ai.OpenAiCarouselWriter;
import com.pawcare.ai.TemplateCarouselWriter;
import com.pawcare.dto.CarouselGenerateRequest;
import com.pawcare.dto.CarouselResponse;
import com.pawcare.dto.CarouselUpdateRequest;
import com.pawcare.dto.SlideDto;
import com.pawcare.exception.ResourceNotFoundException;
import com.pawcare.mapper.DtoMapper;
import com.pawcare.model.Carousel;
import com.pawcare.model.CarouselSlide;
import com.pawcare.repository.CarouselRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CarouselServiceImpl implements CarouselService {

    private static final Logger log = LoggerFactory.getLogger(CarouselServiceImpl.class);

    private final CarouselRepository carouselRepository;
    private final OpenAiCarouselWriter aiWriter;
    private final TemplateCarouselWriter templateWriter;

    @Value("${app.ai.model}")
    private String model;

    /**
     * Uses the AI writer when a key is configured. If the AI call fails (timeout,
     * quota, bad JSON) the founder still gets a draft from the template writer
     * instead of an error, and generatedBy shows which one ran. Not transactional
     * on purpose: no database connection is held while waiting on the AI call;
     * the save at the end runs in its own transaction.
     */
    @Override
    public CarouselResponse generate(CarouselGenerateRequest request) {
        CarouselDraft draft;
        if (aiWriter.isAvailable()) {
            try {
                draft = aiWriter.write(request);
            } catch (RuntimeException e) {
                log.warn("AI writer failed, using the template writer: {}", e.getMessage());
                draft = templateWriter.write(request);
            }
        } else {
            draft = templateWriter.write(request);
        }

        Carousel carousel = new Carousel();
        carousel.setIdea(request.getIdea().trim());
        carousel.setTitle(draft.title());
        carousel.setPlatform(request.getPlatform());
        carousel.setTone(request.getTone());
        carousel.setTheme(request.getTheme());
        carousel.setCaption(draft.caption());
        carousel.setHashtags(draft.hashtags());
        carousel.setGeneratedBy(draft.generatedBy());
        carousel.replaceSlides(toEntities(draft.slides()));

        return DtoMapper.toResponse(carouselRepository.save(carousel));
    }

    @Override
    @Transactional(readOnly = true)
    public CarouselResponse getById(Long id) {
        return DtoMapper.toResponse(find(id));
    }

    @Override
    @Transactional
    public CarouselResponse update(Long id, CarouselUpdateRequest request) {
        Carousel carousel = find(id);
        carousel.setTitle(request.getTitle().trim());
        carousel.setTheme(request.getTheme());
        carousel.setCaption(TextUtil.blankToNull(request.getCaption()));
        carousel.setHashtags(TextUtil.blankToNull(request.getHashtags()));
        if (request.getSlides() != null) {
            carousel.replaceSlides(toEntities(request.getSlides()));
        }
        return DtoMapper.toResponse(carouselRepository.saveAndFlush(carousel));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarouselResponse> recent() {
        return carouselRepository.findTop20ByOrderByCreatedAtDesc().stream().map(DtoMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        carouselRepository.delete(find(id));
    }

    @Override
    public String activeWriter() {
        return aiWriter.isAvailable() ? model : "TEMPLATE";
    }

    private Carousel find(Long id) {
        return carouselRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carousel not found"));
    }

    private static List<CarouselSlide> toEntities(List<SlideDto> slides) {
        List<CarouselSlide> entities = new ArrayList<>();
        for (int i = 0; i < slides.size(); i++) {
            entities.add(DtoMapper.toEntity(slides.get(i), i));
        }
        return entities;
    }
}
