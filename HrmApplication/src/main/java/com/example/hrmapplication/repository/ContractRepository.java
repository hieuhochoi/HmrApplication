package com.example.hrmapplication.repository;

import com.example.hrmapplication.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByEmployeeId(Long employeeId);
    List<Contract> findByStatus(String status);
}