package com.example.hrmapplication.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO phản hồi chấm công.
 */
@Value
@Builder
public class AttendanceResponse {

    Long id;
    Long employeeId;
    String employeeName;
    LocalDate workDate;
    String shift;
    LocalTime checkIn;
    LocalTime checkOut;
    Double workHours;
    Double overtimeHours;
    Boolean isLeave;
    String leaveReason;
    String status;
}

