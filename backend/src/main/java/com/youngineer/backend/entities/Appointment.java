package com.youngineer.backend.entities;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointment_table")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "doctor_user_id")
    private Long doctorId;

    @Column(name = "patient_user_id")
    private Long patientId;

    @Column(name = "time")
    private Date time;

    @Column(name = "is_complete")
    private boolean isComplete = false;

    @Column(name = "is_confirmed")
    private boolean isConfirmed = false;

    @Column(name = "summary")
    private String summary;
}
