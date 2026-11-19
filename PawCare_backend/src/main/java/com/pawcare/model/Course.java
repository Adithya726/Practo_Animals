package com.pawcare.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "course")
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String slug;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String tagline;

    @Column(nullable = false, length = 30)
    private String level;

    /** Who it is built for: STUDENT, PROFESSIONAL or BOTH. */
    @Column(nullable = false, length = 20)
    private String audience;

    @Column(name = "duration_weeks", nullable = false)
    private int durationWeeks;

    @Column(name = "hours_per_week", nullable = false)
    private int hoursPerWeek;

    @Column(nullable = false, length = 40)
    private String mode;

    @Column(name = "price_inr", nullable = false)
    private int priceInr;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "seats_total", nullable = false)
    private int seatsTotal;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "course_outcome", joinColumns = @JoinColumn(name = "course_id"))
    @OrderColumn(name = "position")
    @Column(name = "outcome", length = 200)
    private List<String> outcomes = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "course_tool", joinColumns = @JoinColumn(name = "course_id"))
    @OrderColumn(name = "position")
    @Column(name = "tool", length = 60)
    private List<String> tools = new ArrayList<>();
}
