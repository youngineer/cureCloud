package com.youngineer.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.youngineer.backend.entities.Patient;


@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>{
    public Optional<Patient> findByUserId(Long userId);
}
