package com.example.hrmapplication.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO nhận dữ liệu tạo/cập nhật hợp đồng.
 */
@Data
public class ContractRequest {

    private Long id;

    @NotNull(message = "Thiếu thông tin nhân viên")
    private Long employeeId;

    @NotBlank(message = "Số hợp đồng không được để trống")
    private String contractNumber;

    @NotBlank(message = "Loại hợp đồng không được để trống")
    private String contractType;

    @NotNull(message = "Ngày ký không được để trống")
    private LocalDate signedDate;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDate startDate;

    private LocalDate endDate;

    @Min(value = 1, message = "Thời hạn tối thiểu 1 tháng")
    private Integer duration;

    @NotNull(message = "Mức lương không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Mức lương phải lớn hơn 0")
    private Double salary;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status = "ACTIVE";
}

