package com.pawcare.dto;

import com.pawcare.model.ProviderCategory;

import lombok.Data;

/** Query parameters of GET /api/providers. Every field is optional. */
@Data
public class ProviderSearchCriteria {

    /** Free text matched against name, area and services. */
    private String q;
    private ProviderCategory category;
    private String city;
    private String species;
    private Boolean open24x7;
    private Boolean emergency;
    private Boolean homeVisit;
    private Boolean verified;
    private Double minRating;
    private Integer maxFee;
    /** relevance (default), rating, fee or reviews. */
    private String sort;
    private int page = 0;
    private int size = 12;
}
