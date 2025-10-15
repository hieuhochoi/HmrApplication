package com.example.hrmapplication.repository;

import com.example.hrmapplication.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployeeId(Long employeeId);
    Optional<Salary> findByEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
    List<Salary> findByMonthAndYear(Integer month, Integer year);
}