package com.pawcare.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** A "List Your Business" submission. Approving it publishes a new provider. */
@Entity
@Table(name = "listing_request")
@Getter
@Setter
public class ListingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProviderCategory category;

    @Column(nullable = false, length = 60)
    private String city;

    @Column(nullable = false, length = 80)
    private String area;

    @Column(nullable = false)
    private String address;

    @Column(name = "contact_name", nullable = false, length = 80)
    private String contactName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(length = 1000)
    private String about;

    @Column(name = "fee_from", nullable = false)
    private int feeFrom;

    @Column(name = "open_24x7", nullable = false)
    private boolean open24x7;

    @Column(name = "home_visit", nullable = false)
    private boolean homeVisit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    /** The provider created when the listing was approved. */
    @Column(name = "provider_id")
    private Long providerId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
