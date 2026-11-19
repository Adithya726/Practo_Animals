package com.pawcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pawcare.model.Carousel;

public interface CarouselRepository extends JpaRepository<Carousel, Long> {

    List<Carousel> findTop20ByOrderByCreatedAtDesc();
}
