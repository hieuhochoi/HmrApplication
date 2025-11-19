package com.example.hrmapplication.repository;

import com.example.hrmapplication.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    
    @Query("SELECT DISTINCT lr FROM LeaveRequest lr LEFT JOIN FETCH lr.employee WHERE lr.status = :status")
    List<LeaveRequest> findByStatus(String status);
    
    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, String status);
    List<LeaveRequest> findByStartDateBetween(LocalDate start, LocalDate end);
    
    @Query("SELECT DISTINCT lr FROM LeaveRequest lr LEFT JOIN FETCH lr.employee WHERE lr.status = 'PENDING'")
    List<LeaveRequest> findPendingRequestsWithEmployee();
}

