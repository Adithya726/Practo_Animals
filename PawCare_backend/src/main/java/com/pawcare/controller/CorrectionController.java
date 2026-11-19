package com.pawcare.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pawcare.dto.CorrectionRequest;
import com.pawcare.dto.CorrectionResponse;
import com.pawcare.service.CorrectionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Public "Submit a Correction" form. Review happens in AdminController. */
@RestController
@RequestMapping("/api/corrections")
@RequiredArgsConstructor
public class CorrectionController {

    private final CorrectionService correctionService;

    @PostMapping
    public ResponseEntity<CorrectionResponse> submit(@Valid @RequestBody CorrectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(correctionService.submit(request));
    }
}
