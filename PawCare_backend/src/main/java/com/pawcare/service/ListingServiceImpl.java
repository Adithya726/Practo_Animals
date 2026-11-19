package com.pawcare.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawcare.dto.ListingRequestDto;
import com.pawcare.dto.ListingResponse;
import com.pawcare.exception.BadRequestException;
import com.pawcare.exception.ResourceNotFoundException;
import com.pawcare.mapper.DtoMapper;
import com.pawcare.model.ListingRequest;
import com.pawcare.model.Provider;
import com.pawcare.model.ProviderCategory;
import com.pawcare.model.RequestStatus;
import com.pawcare.repository.ListingRequestRepository;
import com.pawcare.repository.ProviderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final ListingRequestRepository listingRepository;
    private final ProviderRepository providerRepository;

    @Override
    @Transactional
    public ListingResponse submit(ListingRequestDto dto) {
        ListingRequest listing = new ListingRequest();
        listing.setBusinessName(dto.getBusinessName().trim());
        listing.setCategory(dto.getCategory());
        listing.setCity(TextUtil.titleCase(dto.getCity()));
        listing.setArea(dto.getArea().trim());
        listing.setAddress(dto.getAddress().trim());
        listing.setContactName(dto.getContactName().trim());
        listing.setPhone(dto.getPhone().trim());
        listing.setEmail(dto.getEmail().trim().toLowerCase(Locale.ROOT));
        listing.setAbout(TextUtil.blankToNull(dto.getAbout()));
        listing.setFeeFrom(dto.getFeeFrom());
        listing.setOpen24x7(dto.isOpen24x7());
        listing.setHomeVisit(dto.isHomeVisit());
        return DtoMapper.toResponse(listingRepository.save(listing));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListingResponse> listAll() {
        return listingRepository.findAllByOrderByCreatedAtDesc().stream().map(DtoMapper::toResponse).toList();
    }

    /**
     * Moves a pending listing to APPROVED or REJECTED. Approving publishes it as a
     * verified provider straight away, so the business shows up in search.
     */
    @Override
    @Transactional
    public ListingResponse updateStatus(Long id, String status) {
        RequestStatus next = TextUtil.parseEnum(RequestStatus.class, status, "Status must be PENDING, APPROVED or REJECTED");

        ListingRequest listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found"));

        if (listing.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("This listing was already " + listing.getStatus().name().toLowerCase(Locale.ROOT));
        }
        if (next == RequestStatus.PENDING) {
            return DtoMapper.toResponse(listing);
        }

        if (next == RequestStatus.APPROVED) {
            Provider provider = providerRepository.save(toProvider(listing));
            listing.setProviderId(provider.getId());
        }
        listing.setStatus(next);
        return DtoMapper.toResponse(listing);
    }

    static Provider toProvider(ListingRequest l) {
        Provider p = new Provider();
        p.setName(l.getBusinessName());
        p.setCategory(l.getCategory());
        p.setCity(l.getCity());
        p.setArea(l.getArea());
        p.setAddress(l.getAddress());
        p.setPhone(l.getPhone());
        p.setAbout(l.getAbout());
        p.setFeeFrom(l.getFeeFrom());
        p.setOpen24x7(l.isOpen24x7());
        p.setHomeVisit(l.isHomeVisit());
        p.setOpeningHours(l.isOpen24x7() ? "Open 24 hours" : "Hours not added yet");
        p.setEmergency(l.isOpen24x7() || l.getCategory() == ProviderCategory.AMBULANCE);
        p.setVerified(true);
        p.setRating(0);
        p.setReviewCount(0);
        p.setYearsActive(0);
        p.getSpecies().addAll(List.of("Dogs", "Cats"));
        p.getServices().add(l.getCategory().getLabel());
        return p;
    }
}
