package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Salary;
import com.example.hrmapplication.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryService {

    private final SalaryRepository salaryRepository;

    public List<Salary> findByEmployeeId(Long employeeId) {
        return salaryRepository.findByEmployeeId(employeeId);
    }

    public List<Salary> findByMonthYear(Integer month, Integer year) {
        return salaryRepository.findByMonthAndYear(month, year);
    }

    public Salary save(Salary salary) {
        return salaryRepository.save(salary);
    }

    public void delete(Long id) {
        salaryRepository.deleteById(id);
    }
}