package com.example.hrmapplication.repository;

import com.example.hrmapplication.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByCitizenId(String citizenId);
    List<Employee> findByStatus(String status);
    List<Employee> findByFullNameContaining(String fullName);
}