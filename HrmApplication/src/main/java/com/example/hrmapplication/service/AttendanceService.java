package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Attendance;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    public List<Attendance> findByEmployeeId(Long employeeId) {
        if (employeeId == null) {
            return findAll();
        }
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    public List<Attendance> findByDateRange(Long employeeId, LocalDate start, LocalDate end) {
        return attendanceRepository.findByEmployeeIdAndWorkDateBetween(employeeId, start, end);
    }

    public List<Attendance> findByWorkDate(LocalDate workDate) {
        return attendanceRepository.findByWorkDate(workDate);
    }

    public Attendance findById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi chấm công"));
    }

    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public void delete(Long id) {
        attendanceRepository.deleteById(id);
    }

    public List<Attendance> findByEmployeeIdsAndDateRange(List<Long> employeeIds, LocalDate start, LocalDate end) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return List.of();
        }
        return attendanceRepository.findByEmployeeIdsAndWorkDateBetween(employeeIds, start, end);
    }

    public List<Attendance> findByEmployeeIdsAndStatusAndDateRange(List<Long> employeeIds, String status, LocalDate start, LocalDate end) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return List.of();
        }
        return attendanceRepository.findByEmployeeIdsAndStatusAndWorkDateBetween(employeeIds, status, start, end);
    }

    public long countByStatus(List<Long> employeeIds, String status, LocalDate start, LocalDate end) {
        return findByEmployeeIdsAndStatusAndDateRange(employeeIds, status, start, end).size();
    }
}