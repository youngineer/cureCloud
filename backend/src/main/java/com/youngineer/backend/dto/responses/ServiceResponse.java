package com.youngineer.backend.dto.responses;

public record ServiceResponse(boolean isSuccessful, String message, Object data) {
    
}
