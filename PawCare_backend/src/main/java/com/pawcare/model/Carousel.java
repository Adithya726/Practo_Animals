package com.pawcare.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** A carousel produced by the content studio: the idea, its settings and its slides. */
@Entity
@Table(name = "carousel")
@Getter
@Setter
public class Carousel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String idea;

    @Column(nullable = false)
    private String title;

    /** INSTAGRAM or LINKEDIN. */
    @Column(nullable = false, length = 20)
    private String platform;

    @Column(nullable = false, length = 20)
    private String tone;

    /** Name of the visual theme the slides render with. */
    @Column(nullable = false, length = 20)
    private String theme;

    @Column(length = 600)
    private String caption;

    @Column(length = 300)
    private String hashtags;

    /** TEMPLATE when built by the offline engine, otherwise the AI model that wrote it. */
    @Column(name = "generated_by", nullable = false, length = 40)
    private String generatedBy;

    @OneToMany(mappedBy = "carousel", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<CarouselSlide> slides = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void replaceSlides(List<CarouselSlide> newSlides) {
        slides.clear();
        for (CarouselSlide slide : newSlides) {
            slide.setCarousel(this);
            slides.add(slide);
        }
    }
}
