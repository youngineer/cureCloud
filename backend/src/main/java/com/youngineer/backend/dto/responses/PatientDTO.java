package com.youngineer.backend.dto.responses;

import java.sql.Date;

import com.youngineer.backend.entities.Patient;


public record PatientDTO(Long userId, String name, Date dob) {
    public static PatientDTO fromEntity(Patient patient) {
        return new PatientDTO(
            patient.getUserId(),
            patient.getName(),
            patient.getDob()
        );
    }
}
