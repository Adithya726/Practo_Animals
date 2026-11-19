package com.pawcare.mapper;

import java.util.ArrayList;
import java.util.List;

import com.pawcare.dto.CarouselResponse;
import com.pawcare.dto.CorrectionResponse;
import com.pawcare.dto.CourseResponse;
import com.pawcare.dto.LeadResponse;
import com.pawcare.dto.ListingResponse;
import com.pawcare.dto.ProviderResponse;
import com.pawcare.dto.SlideDto;
import com.pawcare.model.Carousel;
import com.pawcare.model.CarouselSlide;
import com.pawcare.model.CorrectionReport;
import com.pawcare.model.Course;
import com.pawcare.model.Lead;
import com.pawcare.model.ListingRequest;
import com.pawcare.model.Provider;

/** Entity-to-DTO mapping in one place, so every service returns the same shapes. */
public final class DtoMapper {

    private DtoMapper() {
    }

    public static ProviderResponse toResponse(Provider p) {
        return new ProviderResponse(
                p.getId(),
                p.getName(),
                p.getCategory(),
                p.getCategory().getLabel(),
                p.getCity(),
                p.getArea(),
                p.getAddress(),
                p.getPhone(),
                p.getAbout(),
                p.getOpeningHours(),
                p.getFeeFrom(),
                p.getRating(),
                p.getReviewCount(),
                p.getYearsActive(),
                p.isOpen24x7(),
                p.isEmergency(),
                p.isHomeVisit(),
                p.isVerified(),
                new ArrayList<>(p.getSpecies()),
                new ArrayList<>(p.getServices()),
                p.getUpdatedAt());
    }

    public static ListingResponse toResponse(ListingRequest l) {
        return new ListingResponse(
                l.getId(),
                l.getBusinessName(),
                l.getCategory(),
                l.getCategory().getLabel(),
                l.getCity(),
                l.getArea(),
                l.getAddress(),
                l.getContactName(),
                l.getPhone(),
                l.getEmail(),
                l.getAbout(),
                l.getFeeFrom(),
                l.isOpen24x7(),
                l.isHomeVisit(),
                l.getStatus(),
                l.getProviderId(),
                l.getCreatedAt());
    }

    public static CorrectionResponse toResponse(CorrectionReport c) {
        return new CorrectionResponse(
                c.getId(),
                c.getProvider().getId(),
                c.getProvider().getName(),
                c.getField(),
                c.getDetails(),
                c.getReporterEmail(),
                c.getStatus(),
                c.getCreatedAt());
    }

    public static CourseResponse toResponse(Course c, long activeRegistrations) {
        int seatsLeft = (int) Math.max(0, c.getSeatsTotal() - activeRegistrations);
        return new CourseResponse(
                c.getId(),
                c.getSlug(),
                c.getTitle(),
                c.getTagline(),
                c.getLevel(),
                c.getAudience(),
                c.getDurationWeeks(),
                c.getHoursPerWeek(),
                c.getMode(),
                c.getPriceInr(),
                c.getStartDate(),
                c.getSeatsTotal(),
                seatsLeft,
                List.copyOf(c.getOutcomes()),
                List.copyOf(c.getTools()));
    }

    public static LeadResponse toResponse(Lead l) {
        return new LeadResponse(
                l.getId(),
                l.getName(),
                l.getEmail(),
                l.getPhone(),
                l.getBackground(),
                l.getCourse() == null ? null : l.getCourse().getId(),
                l.getCourse() == null ? null : l.getCourse().getTitle(),
                l.getType(),
                l.getMessage(),
                l.getSource(),
                l.getStatus(),
                l.getCreatedAt());
    }

    public static SlideDto toDto(CarouselSlide s) {
        return new SlideDto(s.getKind(), s.getHeadline(), s.getBody(), s.getVisualNote(), s.getIcon());
    }

    public static CarouselSlide toEntity(SlideDto dto, int position) {
        CarouselSlide slide = new CarouselSlide();
        slide.setPosition(position);
        slide.setKind(dto.kind());
        slide.setHeadline(dto.headline().trim());
        slide.setBody(dto.body() == null ? null : dto.body().trim());
        slide.setVisualNote(dto.visualNote());
        slide.setIcon(dto.icon());
        return slide;
    }

    public static CarouselResponse toResponse(Carousel c) {
        return new CarouselResponse(
                c.getId(),
                c.getIdea(),
                c.getTitle(),
                c.getPlatform(),
                c.getTone(),
                c.getTheme(),
                c.getCaption(),
                c.getHashtags(),
                c.getGeneratedBy(),
                c.getSlides().stream().map(DtoMapper::toDto).toList(),
                c.getCreatedAt(),
                c.getUpdatedAt());
    }
}
