package com.pawcare.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pawcare.dto.LeadRequest;
import com.pawcare.dto.LeadResponse;
import com.pawcare.service.LeadService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Lead capture for the course landing page: enquiries and seat registrations. */
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @PostMapping
    public ResponseEntity<LeadResponse> capture(@Valid @RequestBody LeadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.capture(request));
    }
}
