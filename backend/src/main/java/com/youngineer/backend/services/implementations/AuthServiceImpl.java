package com.youngineer.backend.services.implementations;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.youngineer.backend.dto.requests.LoginRequest;
import com.youngineer.backend.dto.requests.SignupRequest;
import com.youngineer.backend.dto.responses.ServiceResponse;
import com.youngineer.backend.entities.Doctor;
import com.youngineer.backend.entities.Patient;
import com.youngineer.backend.entities.Role;
import com.youngineer.backend.entities.User;
import com.youngineer.backend.repositories.DoctorRepository;
import com.youngineer.backend.repositories.PatientRepository;
import com.youngineer.backend.repositories.UserRepository;
import com.youngineer.backend.services.AuthService;
import com.youngineer.backend.utils.JwtHelper;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AuthServiceImpl(UserRepository userRepository, PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public ServiceResponse login(LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(loginRequest.email());
            if(userOptional.isEmpty()) {
                return new ServiceResponse(false, "User not found", null);
            } 

            User user = userOptional.get();
            if(!checkPasword(loginRequest.password(), user.getPassword())) {
                return new ServiceResponse(false, "Invalid password", null);
            }

            return new ServiceResponse(true, "Login successful", null);

        } catch (Exception e) {
            return new ServiceResponse(false, "Login failed: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ServiceResponse signup(SignupRequest signupRequest) {
        Optional<User> userOptional = userRepository.findByEmail(signupRequest.email());
        if(userOptional.isPresent()) {
            return new ServiceResponse(false, "Email already registered", null);
        }

        String passwordHash = hashPassword(signupRequest.password());

        User user = new User();
        user.setEmail(signupRequest.email());
        user.setPassword(passwordHash);
        user.setRole(signupRequest.role());

        User savedUser = userRepository.save(user);
        if (savedUser == null) {
            return new ServiceResponse(false, "Couldn't save user, try again", null);
        }

        if(savedUser.getRole() == Role.PATIENT) {
            Patient patient = new Patient();
            patient.setUserId(savedUser.getId());
            patientRepository.save(patient);
        } else if(savedUser.getRole() == Role.DOCTOR) {
            Doctor doctor = new Doctor();
            doctor.setUserId(savedUser.getId());
            doctorRepository.save(doctor);
        }

        return new ServiceResponse(true, "User saved successfully!", null);
    }

    public String generateAccessToken(String email) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if(userOptional.isEmpty()) throw new EntityNotFoundException("No user with such email");

            User user = userOptional.get();
            return JwtHelper.generateToken(email, user.getId());
        } catch (Exception e) {
            return null;
        }
    }

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(password, salt);
    }

    private boolean checkPasword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    protected Long fetchUserID(String email) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            if(userOptional.isPresent()) {
                User user = userOptional.get();
                return user.getId();
            }

            throw new Exception("Error fetching userId");
        } catch (Exception e) {
            return null;
        }
    }
}