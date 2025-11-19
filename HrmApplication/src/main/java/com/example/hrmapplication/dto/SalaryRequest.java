package com.example.hrmapplication.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO nhận dữ liệu bảng lương.
 */
@Data
public class SalaryRequest {

    private Long id;

    @NotNull(message = "Thiếu thông tin nhân viên")
    private Long employeeId;

    @NotNull(message = "Tháng không được bỏ trống")
    @Min(value = 1, message = "Tháng không hợp lệ")
    @Max(value = 12, message = "Tháng không hợp lệ")
    private Integer month;

    @NotNull(message = "Năm không được bỏ trống")
    @Min(value = 2000, message = "Năm không hợp lệ")
    @Max(value = 2100, message = "Năm không hợp lệ")
    private Integer year;

    @DecimalMin(value = "0.0", inclusive = true, message = "Lương cơ bản không âm")
    private Double baseSalary = 0.0;

    @DecimalMin(value = "0.0", inclusive = true, message = "Phụ cấp không âm")
    private Double allowance = 0.0;

    @DecimalMin(value = "0.0", inclusive = true, message = "Thưởng không âm")
    private Double bonus = 0.0;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tăng ca không âm")
    private Double overtime = 0.0;

    @DecimalMin(value = "0.0", inclusive = true, message = "Ứng lương không âm")
    private Double advance = 0.0;

    @DecimalMin(value = "0.0", inclusive = true, message = "Khấu trừ không âm")
    private Double deduction = 0.0;

    private LocalDate paymentDate;

    private String status = "PENDING";
}

