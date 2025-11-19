package com.example.hrmapplication.mapper;

import com.example.hrmapplication.dto.DepartmentRequest;
import com.example.hrmapplication.dto.DepartmentResponse;
import com.example.hrmapplication.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentRequest request) {
        if (request == null) {
            return null;
        }
        Department department = new Department();
        department.setId(request.getId());
        applyCommonFields(department, request);
        return department;
    }

    public DepartmentResponse toResponse(Department department) {
        if (department == null) {
            return null;
        }
        return DepartmentResponse.builder()
                .id(department.getId())
                .departmentCode(department.getDepartmentCode())
                .departmentName(department.getDepartmentName())
                .description(department.getDescription())
                .build();
    }

    public DepartmentRequest toRequest(Department department) {
        if (department == null) {
            return null;
        }
        DepartmentRequest request = new DepartmentRequest();
        request.setId(department.getId());
        request.setDepartmentCode(department.getDepartmentCode());
        request.setDepartmentName(department.getDepartmentName());
        request.setDescription(department.getDescription());
        return request;
    }

    public void updateEntity(Department department, DepartmentRequest request) {
        if (department == null || request == null) {
            return;
        }
        applyCommonFields(department, request);
    }

    private void applyCommonFields(Department department, DepartmentRequest request) {
        department.setDepartmentCode(request.getDepartmentCode());
        department.setDepartmentName(request.getDepartmentName());
        department.setDescription(request.getDescription());
    }
}

