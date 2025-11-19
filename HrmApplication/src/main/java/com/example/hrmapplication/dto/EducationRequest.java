package com.example.hrmapplication.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO nhận dữ liệu trình độ học vấn.
 */
@Data
public class EducationRequest {

    private Long id;

    @NotNull(message = "Thiếu thông tin nhân viên")
    private Long employeeId;

    @Size(max = 50, message = "Thành phần bản thân tối đa 50 ký tự")
    private String familyBackground;

    @Size(max = 50, message = "Trình độ văn hóa tối đa 50 ký tự")
    private String culturalLevel;

    @Size(max = 100, message = "Chuyên môn tối đa 100 ký tự")
    private String specialization;

    @Size(max = 50, message = "Ngoại ngữ tối đa 50 ký tự")
    private String foreignLanguage;

    @Size(max = 50, message = "Trình độ ngoại ngữ tối đa 50 ký tự")
    private String languageLevel;

    @Size(max = 100, message = "Bằng cấp tối đa 100 ký tự")
    private String degree;

    @Size(max = 150, message = "Trường đào tạo tối đa 150 ký tự")
    private String university;

    @Min(value = 1950, message = "Năm tốt nghiệp không hợp lệ")
    @Max(value = 2100, message = "Năm tốt nghiệp không hợp lệ")
    private Integer graduationYear;
}

