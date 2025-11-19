package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.entity.LeaveRequest;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.EmployeeRepository;
import com.example.hrmapplication.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<LeaveRequest> findAll() {
        return leaveRequestRepository.findAll();
    }

    public LeaveRequest findById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu nghỉ phép với ID: " + id));
    }

    public List<LeaveRequest> findByEmployeeId(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    public List<LeaveRequest> findByStatus(String status) {
        return leaveRequestRepository.findByStatus(status);
    }

    public List<LeaveRequest> findPendingRequests() {
        // Sử dụng query với JOIN FETCH để load employee cùng lúc, tránh LazyInitializationException
        return leaveRequestRepository.findPendingRequestsWithEmployee();
    }

    public LeaveRequest save(LeaveRequest leaveRequest) {
        if (leaveRequest.getTotalDays() == null && leaveRequest.getStartDate() != null && leaveRequest.getEndDate() != null) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;
            leaveRequest.setTotalDays((double) days);
        }
        return leaveRequestRepository.save(leaveRequest);
    }

    public LeaveRequest createLeaveRequest(Long employeeId, LeaveRequest leaveRequest) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + employeeId));
        leaveRequest.setEmployee(employee);
        leaveRequest.setStatus("PENDING");
        return save(leaveRequest);
    }

    public LeaveRequest approveLeaveRequest(Long requestId, Long approvedById) {
        LeaveRequest request = findById(requestId);
        Employee approver = employeeRepository.findById(approvedById)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người phê duyệt với ID: " + approvedById));
        
        request.setStatus("APPROVED");
        request.setApprovedBy(approver);
        request.setApprovedAt(LocalDateTime.now());
        
        return save(request);
    }

    public LeaveRequest rejectLeaveRequest(Long requestId, Long approvedById, String rejectionReason) {
        LeaveRequest request = findById(requestId);
        Employee approver = employeeRepository.findById(approvedById)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người phê duyệt với ID: " + approvedById));
        
        request.setStatus("REJECTED");
        request.setApprovedBy(approver);
        request.setApprovedAt(LocalDateTime.now());
        request.setRejectionReason(rejectionReason);
        
        return save(request);
    }

    public void delete(Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy yêu cầu nghỉ phép với ID: " + id);
        }
        leaveRequestRepository.deleteById(id);
    }

    public List<LeaveRequest> findByDateRange(LocalDate start, LocalDate end) {
        return leaveRequestRepository.findByStartDateBetween(start, end);
    }

    /**
     * Tính số ngày phép năm đã sử dụng trong năm hiện tại
     */
    public Double getUsedAnnualLeaveDays(Long employeeId) {
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        LocalDate yearStart = LocalDate.of(currentYear, 1, 1);
        LocalDate yearEnd = LocalDate.of(currentYear, 12, 31);
        
        List<LeaveRequest> approvedAnnualLeaves = leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, "APPROVED")
                .stream()
                .filter(lr -> "ANNUAL".equals(lr.getLeaveType()) 
                        && lr.getStartDate() != null
                        && lr.getStartDate().getYear() == currentYear)
                .toList();
        
        return approvedAnnualLeaves.stream()
                .mapToDouble(lr -> lr.getTotalDays() != null ? lr.getTotalDays() : 0.0)
                .sum();
    }

    /**
     * Tính số ngày phép năm còn lại
     * Mặc định mỗi nhân viên có 12 ngày phép/năm
     */
    public Double getRemainingAnnualLeaveDays(Long employeeId) {
        double totalAnnualLeaveDays = 12.0; // Có thể lấy từ MasterData hoặc SystemConfig sau
        double usedDays = getUsedAnnualLeaveDays(employeeId);
        return Math.max(0, totalAnnualLeaveDays - usedDays);
    }

    /**
     * Lấy tổng số ngày phép năm được cấp (mặc định 12 ngày)
     */
    public Double getTotalAnnualLeaveDays(Long employeeId) {
        return 12.0; // Có thể lấy từ MasterData hoặc SystemConfig sau
    }
}

