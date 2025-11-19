package com.example.hrmapplication.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * DTO phản hồi thông tin hợp đồng cho giao diện.
 */
@Value
@Builder
public class ContractResponse {

    Long id;
    Long employeeId;
    String employeeName;
    String contractNumber;
    String contractType;
    LocalDate signedDate;
    LocalDate startDate;
    LocalDate endDate;
    Integer duration;
    Double salary;
    String status;
}

