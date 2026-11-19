package com.pawcare.service;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawcare.dto.CategoryCount;
import com.pawcare.dto.PageResponse;
import com.pawcare.dto.ProviderResponse;
import com.pawcare.dto.ProviderSearchCriteria;
import com.pawcare.dto.SearchMetaResponse;
import com.pawcare.exception.BadRequestException;
import com.pawcare.exception.ResourceNotFoundException;
import com.pawcare.mapper.DtoMapper;
import com.pawcare.model.Provider;
import com.pawcare.model.ProviderCategory;
import com.pawcare.repository.ProviderRepository;
import com.pawcare.repository.ProviderSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    static final int MAX_PAGE_SIZE = 48;

    private final ProviderRepository providerRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProviderResponse> search(ProviderSearchCriteria criteria) {
        if (criteria.getPage() < 0) {
            throw new BadRequestException("Page cannot be negative");
        }
        if (criteria.getMinRating() != null && (criteria.getMinRating() < 0 || criteria.getMinRating() > 5)) {
            throw new BadRequestException("Rating must be between 0 and 5");
        }
        int size = Math.min(Math.max(criteria.getSize(), 1), MAX_PAGE_SIZE);

        PageRequest pageRequest = PageRequest.of(criteria.getPage(), size, sortFor(criteria.getSort()));
        Page<Provider> page = providerRepository.findAll(ProviderSpecifications.matching(criteria), pageRequest);

        return PageResponse.of(page.map(DtoMapper::toResponse));
    }

    /** "Relevance" puts verified, emergency-ready, well-rated providers first. Id breaks ties so paging is stable. */
    static Sort sortFor(String sort) {
        String key = sort == null ? "relevance" : sort.trim().toLowerCase();
        return switch (key) {
            case "rating" -> Sort.by(Sort.Order.desc("rating"), Sort.Order.desc("reviewCount"), Sort.Order.asc("id"));
            case "fee" -> Sort.by(Sort.Order.asc("feeFrom"), Sort.Order.desc("rating"), Sort.Order.asc("id"));
            case "reviews" -> Sort.by(Sort.Order.desc("reviewCount"), Sort.Order.asc("id"));
            case "relevance", "" -> Sort.by(
                    Sort.Order.desc("verified"),
                    Sort.Order.desc("rating"),
                    Sort.Order.desc("reviewCount"),
                    Sort.Order.asc("id"));
            default -> throw new BadRequestException("Sort must be relevance, rating, fee or reviews");
        };
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderResponse getById(Long id) {
        return providerRepository.findById(id)
                .map(DtoMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProviderResponse> featured() {
        // The best verified provider of each kind (vet, hospital, boarding...), so the
        // home page shows the range of care rather than six of the same thing.
        Map<ProviderCategory, Provider> best = new EnumMap<>(ProviderCategory.class);
        for (Provider p : providerRepository.findByVerifiedTrueOrderByRatingDescReviewCountDesc()) {
            best.putIfAbsent(p.getCategory(), p);
        }
        return best.values().stream().map(DtoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SearchMetaResponse meta() {
        Map<ProviderCategory, Long> counts = new EnumMap<>(ProviderCategory.class);
        for (Object[] row : providerRepository.countByCategory()) {
            counts.put((ProviderCategory) row[0], (Long) row[1]);
        }

        // Every category is listed, even with no providers yet, so the UI tiles never shift around.
        List<CategoryCount> categories = Arrays.stream(ProviderCategory.values())
                .map(c -> new CategoryCount(c, c.getLabel(), counts.getOrDefault(c, 0L)))
                .toList();

        return new SearchMetaResponse(
                categories,
                providerRepository.findDistinctCities(),
                providerRepository.findDistinctSpecies(),
                providerRepository.count());
    }
}
