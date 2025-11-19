package com.example.hrmapplication.mapper;

import com.example.hrmapplication.dto.SalaryRequest;
import com.example.hrmapplication.dto.SalaryResponse;
import com.example.hrmapplication.entity.Salary;
import org.springframework.stereotype.Component;

@Component
public class SalaryMapper {

    public Salary toEntity(SalaryRequest request) {
        if (request == null) {
            return null;
        }
        Salary salary = new Salary();
        salary.setId(request.getId());
        applyCommonFields(salary, request);
        return salary;
    }

    public SalaryResponse toResponse(Salary salary) {
        if (salary == null) {
            return null;
        }
        return SalaryResponse.builder()
                .id(salary.getId())
                .employeeId(salary.getEmployee() != null ? salary.getEmployee().getId() : null)
                .employeeName(salary.getEmployee() != null ? salary.getEmployee().getFullName() : null)
                .month(salary.getMonth())
                .year(salary.getYear())
                .baseSalary(salary.getBaseSalary())
                .allowance(salary.getAllowance())
                .bonus(salary.getBonus())
                .overtime(salary.getOvertime())
                .advance(salary.getAdvance())
                .deduction(salary.getDeduction())
                .totalSalary(salary.getTotalSalary())
                .paymentDate(salary.getPaymentDate())
                .status(salary.getStatus())
                .build();
    }

    public SalaryRequest toRequest(Salary salary) {
        if (salary == null) {
            return null;
        }
        SalaryRequest request = new SalaryRequest();
        request.setId(salary.getId());
        request.setEmployeeId(salary.getEmployee() != null ? salary.getEmployee().getId() : null);
        request.setMonth(salary.getMonth());
        request.setYear(salary.getYear());
        request.setBaseSalary(salary.getBaseSalary());
        request.setAllowance(salary.getAllowance());
        request.setBonus(salary.getBonus());
        request.setOvertime(salary.getOvertime());
        request.setAdvance(salary.getAdvance());
        request.setDeduction(salary.getDeduction());
        request.setPaymentDate(salary.getPaymentDate());
        request.setStatus(salary.getStatus());
        return request;
    }

    public void updateEntity(Salary entity, SalaryRequest request) {
        if (entity == null || request == null) {
            return;
        }
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(Salary target, SalaryRequest request) {
        target.setMonth(request.getMonth());
        target.setYear(request.getYear());
        target.setBaseSalary(request.getBaseSalary());
        target.setAllowance(request.getAllowance());
        target.setBonus(request.getBonus());
        target.setOvertime(request.getOvertime());
        target.setAdvance(request.getAdvance());
        target.setDeduction(request.getDeduction());
        target.setPaymentDate(request.getPaymentDate());
        target.setStatus(request.getStatus());
    }
}

