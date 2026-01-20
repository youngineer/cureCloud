package com.youngineer.backend.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.youngineer.backend.dto.responses.DoctorDTO;
import com.youngineer.backend.dto.responses.ServiceResponse;
import com.youngineer.backend.entities.Doctor;
import com.youngineer.backend.entities.Patient;
import com.youngineer.backend.repositories.DoctorRepository;
import com.youngineer.backend.repositories.PatientRepository;
import com.youngineer.backend.services.PatientServices;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PatientServicesImpl implements PatientServices{
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PatientServicesImpl(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public ServiceResponse getProfile(Long userId) {
        try {
            Optional<Patient> patientOptional = patientRepository.findByUserId(userId);
            if(patientOptional.isEmpty()) throw new EntityNotFoundException("User not found");

            Patient patient = patientOptional.get();
            return new ServiceResponse(true, "Patient fetched successfully!", patient);

        } catch (Exception e) {
            return new ServiceResponse(false, "Error fetching patient profile: " + e.getMessage(), null);
        }
    }


    public ServiceResponse getDoctorList() {
        try {
            List<Doctor> doctorList = doctorRepository.findAll();
    
            List<DoctorDTO> doctorDTOs = doctorList.stream()
                .map(DoctorDTO::fromEntity)
                .collect(Collectors.toList());
            
            return new ServiceResponse(true, "OK", doctorDTOs);
        } catch (Exception e) {
            return new ServiceResponse(false, "Error fetching doctor list: " + e.getMessage(), null);
        }
    }


    public ServiceResponse getDoctor(Long userId) {
        try {
            Optional<Doctor> doctorOptional = doctorRepository.findByUserId(userId);
            if(doctorOptional.isEmpty()) throw new EntityNotFoundException("Error fetching doctor");
    
            Doctor doctor = doctorOptional.get();
            return new ServiceResponse(true, "OK", DoctorDTO.fromEntity(doctor));
        } catch (Exception e) {
            return new ServiceResponse(false, "Error fetching doctor: " + e.getMessage(), null);
        }
    }
}
