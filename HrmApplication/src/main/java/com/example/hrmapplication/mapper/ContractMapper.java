package com.example.hrmapplication.mapper;

import com.example.hrmapplication.dto.ContractRequest;
import com.example.hrmapplication.dto.ContractResponse;
import com.example.hrmapplication.entity.Contract;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    public Contract toEntity(ContractRequest request) {
        if (request == null) {
            return null;
        }
        Contract contract = new Contract();
        contract.setId(request.getId());
        applyCommonFields(contract, request);
        return contract;
    }

    public ContractResponse toResponse(Contract contract) {
        if (contract == null) {
            return null;
        }
        return ContractResponse.builder()
                .id(contract.getId())
                .employeeId(contract.getEmployee() != null ? contract.getEmployee().getId() : null)
                .employeeName(contract.getEmployee() != null ? contract.getEmployee().getFullName() : null)
                .contractNumber(contract.getContractNumber())
                .contractType(contract.getContractType())
                .signedDate(contract.getSignedDate())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .duration(contract.getDuration())
                .salary(contract.getSalary())
                .status(contract.getStatus())
                .build();
    }

    public ContractRequest toRequest(Contract contract) {
        if (contract == null) {
            return null;
        }
        ContractRequest request = new ContractRequest();
        request.setId(contract.getId());
        request.setEmployeeId(contract.getEmployee() != null ? contract.getEmployee().getId() : null);
        request.setContractNumber(contract.getContractNumber());
        request.setContractType(contract.getContractType());
        request.setSignedDate(contract.getSignedDate());
        request.setStartDate(contract.getStartDate());
        request.setEndDate(contract.getEndDate());
        request.setDuration(contract.getDuration());
        request.setSalary(contract.getSalary());
        request.setStatus(contract.getStatus() != null ? contract.getStatus() : "ACTIVE");
        return request;
    }

    public void updateEntity(Contract entity, ContractRequest request) {
        if (entity == null || request == null) {
            return;
        }
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(Contract target, ContractRequest request) {
        target.setContractNumber(request.getContractNumber());
        target.setContractType(request.getContractType());
        target.setSignedDate(request.getSignedDate());
        target.setStartDate(request.getStartDate());
        target.setEndDate(request.getEndDate());
        target.setDuration(request.getDuration());
        target.setSalary(request.getSalary());
        target.setStatus(request.getStatus());
    }
}

