package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService implements CrudService<Employee, Long> {

    private final EmployeeRepository employeeRepository;

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID nhân viên không được để trống");
        }
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + id));
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> search(String keyword) {
        return employeeRepository.findByFullNameContaining(keyword);
    }
}