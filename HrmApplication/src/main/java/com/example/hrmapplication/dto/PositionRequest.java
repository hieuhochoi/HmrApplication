package com.example.hrmapplication.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO nhận dữ liệu tạo/cập nhật chức vụ.
 */
@Data
public class PositionRequest {

    private Long id;

    @NotBlank(message = "Mã chức vụ không được để trống")
    @Size(max = 20, message = "Mã chức vụ tối đa 20 ký tự")
    private String positionCode;

    @NotBlank(message = "Tên chức vụ không được để trống")
    @Size(max = 100, message = "Tên chức vụ tối đa 100 ký tự")
    private String positionName;

    @DecimalMin(value = "0.0", inclusive = false, message = "Lương cơ bản phải lớn hơn 0")
    private Double baseSalary;

    @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
    private String description;
}

