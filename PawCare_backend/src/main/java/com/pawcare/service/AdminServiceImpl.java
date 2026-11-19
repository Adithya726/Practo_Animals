package com.pawcare.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawcare.dto.AdminSummaryResponse;
import com.pawcare.model.LeadStatus;
import com.pawcare.model.LeadType;
import com.pawcare.model.RequestStatus;
import com.pawcare.repository.CarouselRepository;
import com.pawcare.repository.CorrectionReportRepository;
import com.pawcare.repository.LeadRepository;
import com.pawcare.repository.ListingRequestRepository;
import com.pawcare.repository.ProviderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ProviderRepository providerRepository;
    private final ListingRequestRepository listingRepository;
    private final CorrectionReportRepository correctionRepository;
    private final LeadRepository leadRepository;
    private final CarouselRepository carouselRepository;

    @Override
    @Transactional(readOnly = true)
    public AdminSummaryResponse summary() {
        return new AdminSummaryResponse(
                providerRepository.count(),
                listingRepository.countByStatus(RequestStatus.PENDING),
                correctionRepository.countByStatus(RequestStatus.PENDING),
                leadRepository.countByStatus(LeadStatus.NEW),
                leadRepository.countByType(LeadType.REGISTRATION),
                leadRepository.countByType(LeadType.ENQUIRY),
                carouselRepository.count());
    }
}
