package com.example.hrmapplication.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * DTO phản hồi bảng lương.
 */
@Value
@Builder
public class SalaryResponse {

    Long id;
    Long employeeId;
    String employeeName;
    Integer month;
    Integer year;
    Double baseSalary;
    Double allowance;
    Double bonus;
    Double overtime;
    Double advance;
    Double deduction;
    Double totalSalary;
    LocalDate paymentDate;
    String status;
}

