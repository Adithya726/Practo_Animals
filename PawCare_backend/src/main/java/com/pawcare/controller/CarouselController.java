package com.pawcare.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pawcare.dto.CarouselGenerateRequest;
import com.pawcare.dto.CarouselResponse;
import com.pawcare.dto.CarouselUpdateRequest;
import com.pawcare.service.CarouselService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** The content studio: idea in, editable carousel out. */
@RestController
@RequestMapping("/api/carousels")
@RequiredArgsConstructor
public class CarouselController {

    private final CarouselService carouselService;

    @PostMapping("/generate")
    public ResponseEntity<CarouselResponse> generate(@Valid @RequestBody CarouselGenerateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carouselService.generate(request));
    }

    @GetMapping
    public ResponseEntity<List<CarouselResponse>> recent() {
        return ResponseEntity.ok(carouselService.recent());
    }

    @GetMapping("/engine")
    public ResponseEntity<Map<String, String>> engine() {
        return ResponseEntity.ok(Map.of("writer", carouselService.activeWriter()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarouselResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(carouselService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarouselResponse> update(
            @PathVariable Long id, @Valid @RequestBody CarouselUpdateRequest request) {
        return ResponseEntity.ok(carouselService.update(id, request));
    }
}
