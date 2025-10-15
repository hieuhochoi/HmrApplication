package com.example.hrmapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "educations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "family_background")
    private String familyBackground;

    @Column(name = "cultural_level")
    private String culturalLevel;

    private String specialization;

    @Column(name = "foreign_language")
    private String foreignLanguage;

    @Column(name = "language_level")
    private String languageLevel;

    private String degree;

    private String university;

    @Column(name = "graduation_year")
    private Integer graduationYear;
}