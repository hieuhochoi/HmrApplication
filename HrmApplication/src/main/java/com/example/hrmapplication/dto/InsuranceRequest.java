package com.example.hrmapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO nhận dữ liệu tạo/cập nhật bảo hiểm.
 */
@Data
public class InsuranceRequest {

    private Long id;

    @NotNull(message = "Thiếu thông tin nhân viên")
    private Long employeeId;

    @NotBlank(message = "Số bảo hiểm không được để trống")
    @Pattern(regexp = "^[0-9A-Za-z]{8,20}$", message = "Số bảo hiểm phải từ 8-20 ký tự, gồm chữ và số")
    private String insuranceNumber;

    @NotBlank(message = "Loại bảo hiểm không được để trống")
    private String insuranceType;

    private String issuedPlace;

    private LocalDate issuedDate;

    private String medicalFacility;
}

