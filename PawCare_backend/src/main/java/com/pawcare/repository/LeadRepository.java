package com.pawcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pawcare.model.Lead;
import com.pawcare.model.LeadStatus;
import com.pawcare.model.LeadType;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    @EntityGraph(attributePaths = "course")
    List<Lead> findAllByOrderByCreatedAtDesc();

    long countByStatus(LeadStatus status);

    long countByType(LeadType type);

    @Query("select count(l) from Lead l where l.course.id = :courseId and l.type = com.pawcare.model.LeadType.REGISTRATION and l.status <> com.pawcare.model.LeadStatus.LOST")
    long countActiveRegistrations(Long courseId);

    boolean existsByEmailAndCourseIdAndType(String email, Long courseId, LeadType type);
}
