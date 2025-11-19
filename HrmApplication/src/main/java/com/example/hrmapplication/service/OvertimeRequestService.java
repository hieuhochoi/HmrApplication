package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.entity.OvertimeRequest;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.EmployeeRepository;
import com.example.hrmapplication.repository.OvertimeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OvertimeRequestService {

    @Autowired
    private OvertimeRequestRepository overtimeRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<OvertimeRequest> findAll() {
        return overtimeRequestRepository.findAll();
    }

    public OvertimeRequest findById(Long id) {
        return overtimeRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu tăng ca với ID: " + id));
    }

    public List<OvertimeRequest> findByEmployeeId(Long employeeId) {
        return overtimeRequestRepository.findByEmployeeId(employeeId);
    }

    public List<OvertimeRequest> findByStatus(String status) {
        return overtimeRequestRepository.findByStatus(status);
    }

    public List<OvertimeRequest> findPendingRequests() {
        // Sử dụng query với JOIN FETCH để load employee cùng lúc, tránh LazyInitializationException
        return overtimeRequestRepository.findPendingRequestsWithEmployee();
    }

    public OvertimeRequest save(OvertimeRequest overtimeRequest) {
        if (overtimeRequest.getTotalHours() == null && overtimeRequest.getStartTime() != null && overtimeRequest.getEndTime() != null) {
            long minutes = java.time.temporal.ChronoUnit.MINUTES.between(overtimeRequest.getStartTime(), overtimeRequest.getEndTime());
            overtimeRequest.setTotalHours(minutes / 60.0);
        }
        return overtimeRequestRepository.save(overtimeRequest);
    }

    public OvertimeRequest createOvertimeRequest(Long employeeId, OvertimeRequest overtimeRequest) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + employeeId));
        overtimeRequest.setEmployee(employee);
        overtimeRequest.setStatus("PENDING");
        return save(overtimeRequest);
    }

    public OvertimeRequest approveOvertimeRequest(Long requestId, Long approvedById) {
        OvertimeRequest request = findById(requestId);
        Employee approver = employeeRepository.findById(approvedById)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người phê duyệt với ID: " + approvedById));
        
        request.setStatus("APPROVED");
        request.setApprovedBy(approver);
        request.setApprovedAt(LocalDateTime.now());
        
        return save(request);
    }

    public OvertimeRequest rejectOvertimeRequest(Long requestId, Long approvedById, String rejectionReason) {
        OvertimeRequest request = findById(requestId);
        Employee approver = employeeRepository.findById(approvedById)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người phê duyệt với ID: " + approvedById));
        
        request.setStatus("REJECTED");
        request.setApprovedBy(approver);
        request.setApprovedAt(LocalDateTime.now());
        request.setRejectionReason(rejectionReason);
        
        return save(request);
    }

    public void delete(Long id) {
        if (!overtimeRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy yêu cầu tăng ca với ID: " + id);
        }
        overtimeRequestRepository.deleteById(id);
    }

    public List<OvertimeRequest> findByDateRange(LocalDate start, LocalDate end) {
        return overtimeRequestRepository.findByOvertimeDateBetween(start, end);
    }
}

