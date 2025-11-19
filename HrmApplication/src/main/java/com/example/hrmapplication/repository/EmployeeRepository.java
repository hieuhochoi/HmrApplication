package com.example.hrmapplication.repository;

import com.example.hrmapplication.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByCitizenId(String citizenId);
    Optional<Employee> findByKeycloakUserId(String keycloakUserId);
    Optional<Employee> findByEmail(String email);
    List<Employee> findByStatus(String status);
    Page<Employee> findByStatus(String status, Pageable pageable);
    List<Employee> findByFullNameContaining(String fullName);
    Page<Employee> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
    long countByStatus(String status);
    long countByCurrentDepartmentId(Long departmentId);
    List<Employee> findByCurrentDepartmentId(Long departmentId);
    Page<Employee> findByCurrentDepartmentId(Long departmentId, Pageable pageable);
    List<Employee> findByManagerId(Long managerId);
}