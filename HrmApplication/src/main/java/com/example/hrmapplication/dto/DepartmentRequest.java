package com.example.hrmapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO nhận dữ liệu tạo/cập nhật phòng ban.
 */
@Data
public class DepartmentRequest {

    private Long id;

    @NotBlank(message = "Mã phòng ban không được để trống")
    @Size(max = 20, message = "Mã phòng ban tối đa 20 ký tự")
    private String departmentCode;

    @NotBlank(message = "Tên phòng ban không được để trống")
    @Size(max = 100, message = "Tên phòng ban tối đa 100 ký tự")
    private String departmentName;

    @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
    private String description;
}

