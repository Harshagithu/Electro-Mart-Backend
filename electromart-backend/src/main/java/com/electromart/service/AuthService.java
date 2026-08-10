package com.electromart.service;

import com.electromart.dto.request.LoginRequest;
import com.electromart.dto.request.RegisterRequest;
import com.electromart.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}