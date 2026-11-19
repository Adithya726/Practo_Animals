package com.pawcare.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pawcare.dto.ListingRequestDto;
import com.pawcare.dto.ListingResponse;
import com.pawcare.service.ListingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Public "List Your Business" form. Review happens in AdminController. */
@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    public ResponseEntity<ListingResponse> submit(@Valid @RequestBody ListingRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(listingService.submit(request));
    }
}
