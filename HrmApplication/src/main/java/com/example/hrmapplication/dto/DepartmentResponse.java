package com.example.hrmapplication.dto;

import lombok.Builder;
import lombok.Value;

/**
 * DTO trả về thông tin phòng ban cho view/UI.
 */
@Value
@Builder
public class DepartmentResponse {

    Long id;
    String departmentCode;
    String departmentName;
    String description;
}

