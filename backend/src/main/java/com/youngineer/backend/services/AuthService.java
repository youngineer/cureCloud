package com.youngineer.backend.services;

import com.youngineer.backend.dto.requests.LoginRequest;
import com.youngineer.backend.dto.requests.SignupRequest;
import com.youngineer.backend.dto.responses.ServiceResponse;

public interface AuthService {
    public ServiceResponse login(LoginRequest loginRequest);
    public ServiceResponse signup(SignupRequest signupRequest);
    public String generateAccessToken(String email);
}
