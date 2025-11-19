package com.example.hrmapplication.repository;

import com.example.hrmapplication.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployeeId(Long employeeId);
    List<Attendance> findByWorkDate(LocalDate date);
    List<Attendance> findByEmployeeIdAndWorkDateBetween(Long employeeId, LocalDate start, LocalDate end);
    
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Attendance a WHERE a.employee.id IN :employeeIds AND a.workDate BETWEEN :start AND :end")
    List<Attendance> findByEmployeeIdsAndWorkDateBetween(@org.springframework.data.repository.query.Param("employeeIds") List<Long> employeeIds, 
                                                          @org.springframework.data.repository.query.Param("start") LocalDate start, 
                                                          @org.springframework.data.repository.query.Param("end") LocalDate end);
    
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Attendance a WHERE a.employee.id IN :employeeIds AND a.status = :status AND a.workDate BETWEEN :start AND :end")
    List<Attendance> findByEmployeeIdsAndStatusAndWorkDateBetween(@org.springframework.data.repository.query.Param("employeeIds") List<Long> employeeIds,
                                                                   @org.springframework.data.repository.query.Param("status") String status,
                                                                   @org.springframework.data.repository.query.Param("start") LocalDate start, 
                                                                   @org.springframework.data.repository.query.Param("end") LocalDate end);
}