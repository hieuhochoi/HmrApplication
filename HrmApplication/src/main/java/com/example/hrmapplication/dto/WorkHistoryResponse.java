package com.example.hrmapplication.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

/**
 * DTO phản hồi quá trình công tác.
 */
@Value
@Builder
public class WorkHistoryResponse {

    Long id;
    Long employeeId;
    String employeeName;
    Long departmentId;
    String departmentName;
    Long positionId;
    String positionName;
    LocalDate startDate;
    LocalDate endDate;
    String leaveType;
    String rewardType;
    String disciplineType;
    String note;
}

