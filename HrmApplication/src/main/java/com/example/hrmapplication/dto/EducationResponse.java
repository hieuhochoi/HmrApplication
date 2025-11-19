package com.example.hrmapplication.dto;

import lombok.Builder;
import lombok.Value;

/**
 * DTO phản hồi trình độ học vấn.
 */
@Value
@Builder
public class EducationResponse {

    Long id;
    Long employeeId;
    String employeeName;
    String familyBackground;
    String culturalLevel;
    String specialization;
    String foreignLanguage;
    String languageLevel;
    String degree;
    String university;
    Integer graduationYear;
}

