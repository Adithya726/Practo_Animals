package com.pawcare.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pawcare.dto.AuthResponse;
import com.pawcare.dto.LoginRequest;
import com.pawcare.exception.BadRequestException;
import com.pawcare.model.Admin;
import com.pawcare.repository.AdminRepository;
import com.pawcare.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /** Emails are stored trimmed and lowercase so "A@x.com" and "a@x.com" are one account. */
    static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        Admin admin = adminRepository.findByEmail(normalizeEmail(request.getEmail()))
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        return new AuthResponse(
                jwtUtil.generateToken(admin.getEmail()),
                admin.getId(),
                admin.getName(),
                admin.getEmail());
    }
}
