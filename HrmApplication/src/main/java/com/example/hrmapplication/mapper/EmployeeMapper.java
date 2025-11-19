package com.example.hrmapplication.mapper;

import com.example.hrmapplication.dto.EmployeeRequest;
import com.example.hrmapplication.dto.EmployeeResponse;
import com.example.hrmapplication.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request) {
        if (request == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setId(request.getId());
        applyCommonFields(employee, request);
        return employee;
    }

    public EmployeeResponse toResponse(Employee employee) {
        if (employee == null) {
            return null;
        }
        return EmployeeResponse.builder()
                .id(employee.getId())
                .fullName(employee.getFullName())
                .gender(employee.getGender())
                .dateOfBirth(employee.getDateOfBirth())
                .address(employee.getAddress())
                .citizenId(employee.getCitizenId())
                .birthPlace(employee.getBirthPlace())
                .currentAddress(employee.getCurrentAddress())
                .phone(employee.getPhone())
                .email(employee.getEmail())
                .status(employee.getStatus())
                .build();
    }

    public EmployeeRequest toRequest(Employee employee) {
        if (employee == null) {
            return null;
        }
        EmployeeRequest request = new EmployeeRequest();
        request.setId(employee.getId());
        request.setFullName(employee.getFullName());
        request.setGender(employee.getGender());
        request.setDateOfBirth(employee.getDateOfBirth());
        request.setAddress(employee.getAddress());
        request.setCitizenId(employee.getCitizenId());
        request.setBirthPlace(employee.getBirthPlace());
        request.setCurrentAddress(employee.getCurrentAddress());
        request.setPhone(employee.getPhone());
        request.setEmail(employee.getEmail());
        request.setStatus(employee.getStatus());
        return request;
    }

    public void updateEntity(Employee entity, EmployeeRequest request) {
        if (entity == null || request == null) {
            return;
        }
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(Employee target, EmployeeRequest request) {
        target.setFullName(request.getFullName());
        target.setGender(request.getGender());
        target.setDateOfBirth(request.getDateOfBirth());
        target.setAddress(request.getAddress());
        target.setCitizenId(request.getCitizenId());
        target.setBirthPlace(request.getBirthPlace());
        target.setCurrentAddress(request.getCurrentAddress());
        target.setPhone(request.getPhone());
        target.setEmail(request.getEmail());
        target.setStatus(request.getStatus());
    }
}

