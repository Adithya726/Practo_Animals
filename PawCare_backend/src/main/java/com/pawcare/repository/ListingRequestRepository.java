package com.pawcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pawcare.model.ListingRequest;
import com.pawcare.model.RequestStatus;

public interface ListingRequestRepository extends JpaRepository<ListingRequest, Long> {

    List<ListingRequest> findAllByOrderByCreatedAtDesc();

    long countByStatus(RequestStatus status);
}
