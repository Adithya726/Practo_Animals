package com.pawcare.model;

/** The kinds of animal-care providers the platform lists. */
public enum ProviderCategory {
    VET_CLINIC("Vet Clinic"),
    HOSPITAL("Pet Hospital"),
    BOARDING("Boarding & Daycare"),
    DIAGNOSTICS("Diagnostics & Labs"),
    AMBULANCE("Pet Ambulance"),
    NGO("Rescue & NGO");

    private final String label;

    ProviderCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
