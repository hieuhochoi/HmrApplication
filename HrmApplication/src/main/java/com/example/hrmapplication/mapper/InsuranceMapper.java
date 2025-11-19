package com.example.hrmapplication.mapper;

import com.example.hrmapplication.dto.InsuranceRequest;
import com.example.hrmapplication.dto.InsuranceResponse;
import com.example.hrmapplication.entity.Insurance;
import org.springframework.stereotype.Component;

@Component
public class InsuranceMapper {

    public Insurance toEntity(InsuranceRequest request) {
        if (request == null) {
            return null;
        }
        Insurance insurance = new Insurance();
        insurance.setId(request.getId());
        applyCommonFields(insurance, request);
        return insurance;
    }

    public InsuranceResponse toResponse(Insurance insurance) {
        if (insurance == null) {
            return null;
        }
        return InsuranceResponse.builder()
                .id(insurance.getId())
                .employeeId(insurance.getEmployee() != null ? insurance.getEmployee().getId() : null)
                .employeeName(insurance.getEmployee() != null ? insurance.getEmployee().getFullName() : null)
                .insuranceNumber(insurance.getInsuranceNumber())
                .insuranceType(insurance.getInsuranceType())
                .issuedPlace(insurance.getIssuedPlace())
                .issuedDate(insurance.getIssuedDate())
                .medicalFacility(insurance.getMedicalFacility())
                .build();
    }

    public InsuranceRequest toRequest(Insurance insurance) {
        if (insurance == null) {
            return null;
        }
        InsuranceRequest request = new InsuranceRequest();
        request.setId(insurance.getId());
        request.setEmployeeId(insurance.getEmployee() != null ? insurance.getEmployee().getId() : null);
        request.setInsuranceNumber(insurance.getInsuranceNumber());
        request.setInsuranceType(insurance.getInsuranceType());
        request.setIssuedPlace(insurance.getIssuedPlace());
        request.setIssuedDate(insurance.getIssuedDate());
        request.setMedicalFacility(insurance.getMedicalFacility());
        return request;
    }

    public void updateEntity(Insurance entity, InsuranceRequest request) {
        if (entity == null || request == null) {
            return;
        }
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(Insurance target, InsuranceRequest request) {
        target.setInsuranceNumber(request.getInsuranceNumber());
        target.setInsuranceType(request.getInsuranceType());
        target.setIssuedPlace(request.getIssuedPlace());
        target.setIssuedDate(request.getIssuedDate());
        target.setMedicalFacility(request.getMedicalFacility());
    }
}

