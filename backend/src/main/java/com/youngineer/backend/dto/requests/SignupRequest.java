package com.youngineer.backend.dto.requests;

public record SignupRequest(String email, String password, String name, String userType) {
	
}
