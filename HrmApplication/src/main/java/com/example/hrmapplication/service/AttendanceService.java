package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Attendance;
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

    public List<Attendance> findByEmployeeId(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }

    public List<Attendance> findByDateRange(Long employeeId, LocalDate start, LocalDate end) {
        return attendanceRepository.findByEmployeeIdAndWorkDateBetween(employeeId, start, end);
    }

    public Attendance save(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public void delete(Long id) {
        attendanceRepository.deleteById(id);
    }
}