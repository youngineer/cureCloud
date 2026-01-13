package com.youngineer.backend.services;

import com.youngineer.backend.dto.responses.ServiceResponse;

public interface AuthService {
    public ServiceResponse login(String email, String password);
    public ServiceResponse signup(String email, String password, String name, String userType);
}
