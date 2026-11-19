package com.pawcare.service;

import java.util.List;

import com.pawcare.dto.LeadRequest;
import com.pawcare.dto.LeadResponse;

public interface LeadService {

    LeadResponse capture(LeadRequest request);

    List<LeadResponse> listAll();

    LeadResponse updateStatus(Long id, String status);
}
