package com.youngineer.backend.dto.responses;

import com.youngineer.backend.entities.Doctor;

public record DoctorDTO(Long doctorUserId, String doctorName, String doctorBio) {
    public static DoctorDTO fromEntity(Doctor doctor) {
        return new DoctorDTO(
            doctor.getUserId(),
            doctor.getName(),
            doctor.getBio()
        );
    }
}
