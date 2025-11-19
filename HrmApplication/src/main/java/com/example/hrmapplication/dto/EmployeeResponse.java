package com.example.hrmapplication.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * DTO phản hồi thông tin nhân viên ra view/API.
 */
@Value
@Builder
public class EmployeeResponse {

    Long id;
    String fullName;
    String gender;
    LocalDate dateOfBirth;
    String address;
    String citizenId;
    String birthPlace;
    String currentAddress;
    String phone;
    String email;
    String status;
}

