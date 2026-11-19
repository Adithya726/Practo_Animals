package com.pawcare.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/** A "Submit a Correction" report from a pet parent about a provider's details. */
@Entity
@Table(name = "correction_report")
@Getter
@Setter
public class CorrectionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    /** Which detail is wrong: PHONE, ADDRESS, HOURS, FEE, CLOSED, OTHER. */
    @Column(nullable = false, length = 20)
    private String field;

    @Column(nullable = false, length = 500)
    private String details;

    @Column(name = "reporter_email")
    private String reporterEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
