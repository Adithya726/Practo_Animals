package com.pawcare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pawcare.dto.AdminSummaryResponse;
import com.pawcare.dto.CorrectionResponse;
import com.pawcare.dto.LeadResponse;
import com.pawcare.dto.ListingResponse;
import com.pawcare.dto.StatusUpdateRequest;
import com.pawcare.service.AdminService;
import com.pawcare.service.CarouselService;
import com.pawcare.service.CorrectionService;
import com.pawcare.service.LeadService;
import com.pawcare.service.ListingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** The founder's back office. Every route here needs the admin JWT. */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ListingService listingService;
    private final CorrectionService correctionService;
    private final LeadService leadService;
    private final CarouselService carouselService;

    @GetMapping("/summary")
    public ResponseEntity<AdminSummaryResponse> summary() {
        return ResponseEntity.ok(adminService.summary());
    }

    @GetMapping("/listings")
    public ResponseEntity<List<ListingResponse>> listings() {
        return ResponseEntity.ok(listingService.listAll());
    }

    @PatchMapping("/listings/{id}/status")
    public ResponseEntity<ListingResponse> updateListing(
            @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(listingService.updateStatus(id, request.getStatus()));
    }

    @GetMapping("/corrections")
    public ResponseEntity<List<CorrectionResponse>> corrections() {
        return ResponseEntity.ok(correctionService.listAll());
    }

    @PatchMapping("/corrections/{id}/status")
    public ResponseEntity<CorrectionResponse> updateCorrection(
            @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(correctionService.updateStatus(id, request.getStatus()));
    }

    @GetMapping("/leads")
    public ResponseEntity<List<LeadResponse>> leads() {
        return ResponseEntity.ok(leadService.listAll());
    }

    @PatchMapping("/leads/{id}/status")
    public ResponseEntity<LeadResponse> updateLead(
            @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(leadService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/carousels/{id}")
    public ResponseEntity<Void> deleteCarousel(@PathVariable Long id) {
        carouselService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
