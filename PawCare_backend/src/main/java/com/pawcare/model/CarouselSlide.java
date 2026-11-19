package com.pawcare.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "carousel_slide")
@Getter
@Setter
public class CarouselSlide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carousel_id", nullable = false)
    private Carousel carousel;

    @Column(nullable = false)
    private int position;

    /** HOOK, CONTENT or CTA: decides the layout the slide renders with. */
    @Column(nullable = false, length = 10)
    private String kind;

    @Column(nullable = false, length = 160)
    private String headline;

    @Column(length = 600)
    private String body;

    /** Art direction for the slide: icon, emphasis, imagery. */
    @Column(name = "visual_note", length = 300)
    private String visualNote;

    @Column(length = 30)
    private String icon;
}
