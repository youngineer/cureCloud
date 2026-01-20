package com.youngineer.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youngineer.backend.dto.responses.BackendResponse;
import com.youngineer.backend.dto.responses.ServiceResponse;
import com.youngineer.backend.services.PatientServices;
import com.youngineer.backend.utils.SecurityUtils;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/patient")
public class PatientController {
    private final PatientServices patientServices;

    public PatientController(PatientServices patientServices) {
        this.patientServices = patientServices;
    }


    @GetMapping("/profile")
    public ResponseEntity<BackendResponse> getProfile() {
        try {
            Long userId = SecurityUtils.getCurrentUserId();

            ServiceResponse response = patientServices.getProfile(userId);
            if(response.isSuccessful()) {
                return ResponseEntity.ok(new BackendResponse(response.message(), response.data()));
            }
            return ResponseEntity.badRequest().body(new BackendResponse(response.message(), null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BackendResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/doctorList")
    public ResponseEntity<BackendResponse> getDoctorList() {
        try {
            ServiceResponse response = patientServices.getDoctorList();
            if(response.isSuccessful()) {
                return ResponseEntity.ok(new BackendResponse(response.message(), response.data()));
            }
            return ResponseEntity.badRequest().body(new BackendResponse(response.message(), null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BackendResponse(e.getMessage(), null));
        }
    }


    @GetMapping("/doctor/{userId}")
    public ResponseEntity<BackendResponse> getDoctor(@PathVariable Long userId) {
        try {
            ServiceResponse response = patientServices.getDoctor(userId);
            if(response.isSuccessful()) {
                return ResponseEntity.ok(new BackendResponse(response.message(), response.data()));
            }
            return ResponseEntity.badRequest().body(new BackendResponse(response.message(), null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new BackendResponse(e.getMessage(), null));
        }
    }
    
    
}
