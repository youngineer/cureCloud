package com.youngineer.backend.dto.requests;

import com.youngineer.backend.entities.Role;

public record SignupRequest(String email, String password, String name, Role role) {
	
}
