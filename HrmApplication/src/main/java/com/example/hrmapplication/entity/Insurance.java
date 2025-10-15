package com.example.hrmapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "insurances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "insurance_number", unique = true)
    private String insuranceNumber;

    @Column(name = "issued_place")
    private String issuedPlace;

    @Column(name = "issued_date")
    private LocalDate issuedDate;

    @Column(name = "medical_facility")
    private String medicalFacility;

    @Column(name = "insurance_type")
    private String insuranceType;
}