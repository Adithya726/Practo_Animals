package com.pawcare.dto;

import com.pawcare.model.ProviderCategory;

public record CategoryCount(ProviderCategory category, String label, long count) {
}
