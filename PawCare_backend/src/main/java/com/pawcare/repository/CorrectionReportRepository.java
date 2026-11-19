package com.pawcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pawcare.model.CorrectionReport;
import com.pawcare.model.RequestStatus;

public interface CorrectionReportRepository extends JpaRepository<CorrectionReport, Long> {

    @EntityGraph(attributePaths = "provider")
    List<CorrectionReport> findAllByOrderByCreatedAtDesc();

    long countByStatus(RequestStatus status);
}
