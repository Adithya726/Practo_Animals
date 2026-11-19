package com.pawcare.dto;

public record AdminSummaryResponse(
        long providers,
        long pendingListings,
        long pendingCorrections,
        long newLeads,
        long registrations,
        long enquiries,
        long carousels) {
}
