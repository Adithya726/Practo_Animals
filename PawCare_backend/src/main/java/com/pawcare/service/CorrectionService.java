package com.pawcare.service;

import java.util.List;

import com.pawcare.dto.CorrectionRequest;
import com.pawcare.dto.CorrectionResponse;

public interface CorrectionService {

    CorrectionResponse submit(CorrectionRequest request);

    List<CorrectionResponse> listAll();

    CorrectionResponse updateStatus(Long id, String status);
}
