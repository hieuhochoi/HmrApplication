package com.example.hrmapplication.repository;

import com.example.hrmapplication.entity.OvertimeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OvertimeRequestRepository extends JpaRepository<OvertimeRequest, Long> {
    List<OvertimeRequest> findByEmployeeId(Long employeeId);
    
    @Query("SELECT DISTINCT or FROM OvertimeRequest or LEFT JOIN FETCH or.employee WHERE or.status = :status")
    List<OvertimeRequest> findByStatus(String status);
    
    List<OvertimeRequest> findByEmployeeIdAndStatus(Long employeeId, String status);
    List<OvertimeRequest> findByOvertimeDateBetween(LocalDate start, LocalDate end);
    
    @Query("SELECT DISTINCT or FROM OvertimeRequest or LEFT JOIN FETCH or.employee WHERE or.status = 'PENDING'")
    List<OvertimeRequest> findPendingRequestsWithEmployee();
}

