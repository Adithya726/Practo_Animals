package com.pawcare.service;

import java.util.List;

import com.pawcare.dto.ListingRequestDto;
import com.pawcare.dto.ListingResponse;

public interface ListingService {

    ListingResponse submit(ListingRequestDto request);

    List<ListingResponse> listAll();

    ListingResponse updateStatus(Long id, String status);
}
