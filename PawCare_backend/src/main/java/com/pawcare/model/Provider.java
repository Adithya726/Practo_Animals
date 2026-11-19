package com.pawcare.model;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "provider", indexes = {
        @Index(name = "idx_provider_city", columnList = "city"),
        @Index(name = "idx_provider_category", columnList = "category")
})
@Getter
@Setter
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProviderCategory category;

    @Column(nullable = false, length = 60)
    private String city;

    @Column(nullable = false, length = 80)
    private String area;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 1000)
    private String about;

    @Column(name = "opening_hours", length = 80)
    private String openingHours;

    /** Consultation or starting price in rupees. 0 means free (for example an NGO helpline). */
    @Column(name = "fee_from", nullable = false)
    private int feeFrom;

    @Column(nullable = false)
    private double rating;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "years_active", nullable = false)
    private int yearsActive;

    @Column(name = "open_24x7", nullable = false)
    private boolean open24x7;

    @Column(nullable = false)
    private boolean emergency;

    @Column(name = "home_visit", nullable = false)
    private boolean homeVisit;

    @Column(nullable = false)
    private boolean verified;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "provider_species", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "species", length = 30)
    private Set<String> species = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "provider_service", joinColumns = @JoinColumn(name = "provider_id"))
    @Column(name = "service", length = 60)
    private Set<String> services = new LinkedHashSet<>();

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
}
