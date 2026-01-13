package com.youngineer.backend.services.implementations;

import com.youngineer.backend.dto.responses.ServiceResponse;
import com.youngineer.backend.services.AuthService;


public class AuthServiceImpl implements AuthService {
    public ServiceResponse login(String email, String password) {
        
        return new ServiceResponse(true, "Login successful", null);
    }

    public ServiceResponse signup(String email, String password, String name, String userType) {
        
        return new ServiceResponse(true, "Signup successful", null);
    }
}