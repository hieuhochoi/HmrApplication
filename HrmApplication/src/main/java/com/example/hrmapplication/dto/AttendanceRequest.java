package com.example.hrmapplication.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO nhận dữ liệu chấm công.
 */
@Data
public class AttendanceRequest {

    private Long id;

    @NotNull(message = "Thiếu thông tin nhân viên")
    private Long employeeId;

    @NotNull(message = "Ngày làm việc không được bỏ trống")
    private LocalDate workDate;

    @NotBlank(message = "Ca làm việc không được bỏ trống")
    private String shift;

    private LocalTime checkIn;
    private LocalTime checkOut;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giờ công không âm")
    private Double workHours = 0.0;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giờ tăng ca không âm")
    private Double overtimeHours = 0.0;

    private Boolean isLeave = false;
    private String leaveReason;

    @NotBlank(message = "Trạng thái không được bỏ trống")
    private String status = "PRESENT";
}

