package com.example.hrmapplication.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * DTO phản hồi thông tin bảo hiểm cho giao diện.
 */
@Value
@Builder
public class InsuranceResponse {

    Long id;
    Long employeeId;
    String employeeName;
    String insuranceNumber;
    String insuranceType;
    String issuedPlace;
    LocalDate issuedDate;
    String medicalFacility;
}

