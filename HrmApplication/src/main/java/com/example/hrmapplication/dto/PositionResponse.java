package com.example.hrmapplication.dto;

import lombok.Builder;
import lombok.Value;

/**
 * DTO trả về thông tin chức vụ cho view/UI.
 */
@Value
@Builder
public class PositionResponse {

    Long id;
    String positionCode;
    String positionName;
    Double baseSalary;
    String description;
}

