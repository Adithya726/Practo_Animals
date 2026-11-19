package com.pawcare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pawcare.dto.PageResponse;
import com.pawcare.dto.ProviderResponse;
import com.pawcare.dto.ProviderSearchCriteria;
import com.pawcare.dto.SearchMetaResponse;
import com.pawcare.service.ProviderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    /** Search with optional filters, for example ?city=Hyderabad&category=VET_CLINIC&open24x7=true&sort=rating */
    @GetMapping
    public ResponseEntity<PageResponse<ProviderResponse>> search(@ModelAttribute ProviderSearchCriteria criteria) {
        return ResponseEntity.ok(providerService.search(criteria));
    }

    @GetMapping("/meta")
    public ResponseEntity<SearchMetaResponse> meta() {
        return ResponseEntity.ok(providerService.meta());
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProviderResponse>> featured() {
        return ResponseEntity.ok(providerService.featured());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.getById(id));
    }
}
