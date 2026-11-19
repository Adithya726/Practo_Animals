package com.pawcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.pawcare.model.Provider;
import com.pawcare.model.ProviderCategory;

public interface ProviderRepository extends JpaRepository<Provider, Long>, JpaSpecificationExecutor<Provider> {

    @Query("select distinct p.city from Provider p order by p.city")
    List<String> findDistinctCities();

    @Query("select distinct s from Provider p join p.species s order by s")
    List<String> findDistinctSpecies();

    @Query("select p.category, count(p) from Provider p group by p.category")
    List<Object[]> countByCategory();

    long countByCategory(ProviderCategory category);

    List<Provider> findByVerifiedTrueOrderByRatingDescReviewCountDesc();
}
