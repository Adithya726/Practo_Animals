package com.pawcare.dto;

import java.util.List;

/** Everything the search filters need to render: categories with counts, cities and species. */
public record SearchMetaResponse(
        List<CategoryCount> categories,
        List<String> cities,
        List<String> species,
        long totalProviders) {
}
