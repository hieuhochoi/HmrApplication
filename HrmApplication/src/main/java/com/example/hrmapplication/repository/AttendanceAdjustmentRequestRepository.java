package com.example.hrmapplication.repository;

import com.example.hrmapplication.entity.AttendanceAdjustmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceAdjustmentRequestRepository extends JpaRepository<AttendanceAdjustmentRequest, Long> {
    List<AttendanceAdjustmentRequest> findByEmployeeId(Long employeeId);
    
    @Query("SELECT DISTINCT aar FROM AttendanceAdjustmentRequest aar LEFT JOIN FETCH aar.employee LEFT JOIN FETCH aar.attendance WHERE aar.status = :status")
    List<AttendanceAdjustmentRequest> findByStatus(String status);
    
    List<AttendanceAdjustmentRequest> findByEmployeeIdAndStatus(Long employeeId, String status);
    
    @Query("SELECT DISTINCT aar FROM AttendanceAdjustmentRequest aar LEFT JOIN FETCH aar.employee LEFT JOIN FETCH aar.attendance WHERE aar.status = 'PENDING'")
    List<AttendanceAdjustmentRequest> findPendingRequestsWithEmployee();
}

