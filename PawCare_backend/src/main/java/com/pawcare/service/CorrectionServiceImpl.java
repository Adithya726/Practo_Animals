package com.pawcare.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawcare.dto.CorrectionRequest;
import com.pawcare.dto.CorrectionResponse;
import com.pawcare.exception.BadRequestException;
import com.pawcare.exception.ResourceNotFoundException;
import com.pawcare.mapper.DtoMapper;
import com.pawcare.model.CorrectionReport;
import com.pawcare.model.Provider;
import com.pawcare.model.RequestStatus;
import com.pawcare.repository.CorrectionReportRepository;
import com.pawcare.repository.ProviderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CorrectionServiceImpl implements CorrectionService {

    private final CorrectionReportRepository correctionRepository;
    private final ProviderRepository providerRepository;

    @Override
    @Transactional
    public CorrectionResponse submit(CorrectionRequest request) {
        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        CorrectionReport report = new CorrectionReport();
        report.setProvider(provider);
        report.setField(request.getField());
        report.setDetails(request.getDetails().trim());
        String email = TextUtil.blankToNull(request.getReporterEmail());
        report.setReporterEmail(email == null ? null : email.toLowerCase(Locale.ROOT));

        return DtoMapper.toResponse(correctionRepository.save(report));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CorrectionResponse> listAll() {
        return correctionRepository.findAllByOrderByCreatedAtDesc().stream().map(DtoMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public CorrectionResponse updateStatus(Long id, String status) {
        RequestStatus next = TextUtil.parseEnum(RequestStatus.class, status, "Status must be PENDING, APPROVED or REJECTED");

        CorrectionReport report = correctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Correction not found"));

        if (report.getStatus() != RequestStatus.PENDING && next != report.getStatus()) {
            throw new BadRequestException("This correction was already reviewed");
        }
        report.setStatus(next);
        return DtoMapper.toResponse(report);
    }
}
