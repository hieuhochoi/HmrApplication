package com.example.hrmapplication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO nhận dữ liệu quá trình công tác.
 */
@Data
public class WorkHistoryRequest {

    private Long id;

    @NotNull(message = "Thiếu thông tin nhân viên")
    private Long employeeId;

    private Long departmentId;
    private Long positionId;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    private LocalDate endDate;

    private String leaveType;
    private String rewardType;
    private String disciplineType;
    private String note;
}

