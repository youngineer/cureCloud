package com.youngineer.backend.services;

import com.youngineer.backend.dto.responses.ServiceResponse;

public interface PatientServices {
    public ServiceResponse getProfile(Long userId);
    public ServiceResponse getDoctorList();
    public ServiceResponse getDoctor(Long userId);
}
