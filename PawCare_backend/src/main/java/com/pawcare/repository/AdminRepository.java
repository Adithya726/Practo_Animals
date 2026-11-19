package com.pawcare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pawcare.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);
}
