package com.pawcare.service;

import java.util.List;

import com.pawcare.dto.PageResponse;
import com.pawcare.dto.ProviderResponse;
import com.pawcare.dto.ProviderSearchCriteria;
import com.pawcare.dto.SearchMetaResponse;

public interface ProviderService {

    PageResponse<ProviderResponse> search(ProviderSearchCriteria criteria);

    ProviderResponse getById(Long id);

    List<ProviderResponse> featured();

    SearchMetaResponse meta();
}
