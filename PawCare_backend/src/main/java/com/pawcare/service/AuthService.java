package com.pawcare.service;

import com.pawcare.dto.AuthResponse;
import com.pawcare.dto.LoginRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);
}
