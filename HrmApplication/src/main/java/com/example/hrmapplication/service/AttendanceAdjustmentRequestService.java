package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Attendance;
import com.example.hrmapplication.entity.AttendanceAdjustmentRequest;
import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.AttendanceAdjustmentRequestRepository;
import com.example.hrmapplication.repository.AttendanceRepository;
import com.example.hrmapplication.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class AttendanceAdjustmentRequestService {

    @Autowired
    private AttendanceAdjustmentRequestRepository adjustmentRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<AttendanceAdjustmentRequest> findAll() {
        return adjustmentRequestRepository.findAll();
    }

    public AttendanceAdjustmentRequest findById(Long id) {
        return adjustmentRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu điều chỉnh công với ID: " + id));
    }

    public List<AttendanceAdjustmentRequest> findByEmployeeId(Long employeeId) {
        return adjustmentRequestRepository.findByEmployeeId(employeeId);
    }

    public List<AttendanceAdjustmentRequest> findPendingRequests() {
        return adjustmentRequestRepository.findPendingRequestsWithEmployee();
    }

    public AttendanceAdjustmentRequest createAdjustmentRequest(Long employeeId, Long attendanceId, 
                                                               String newCheckIn, String newCheckOut, 
                                                               String newStatus, String reason) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + employeeId));
        
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi chấm công với ID: " + attendanceId));
        
        AttendanceAdjustmentRequest request = new AttendanceAdjustmentRequest();
        request.setEmployee(employee);
        request.setAttendance(attendance);
        request.setRequestDate(java.time.LocalDate.now());
        request.setOldCheckIn(attendance.getCheckIn() != null ? attendance.getCheckIn().toString() : null);
        request.setOldCheckOut(attendance.getCheckOut() != null ? attendance.getCheckOut().toString() : null);
        request.setOldStatus(attendance.getStatus());
        request.setNewCheckIn(newCheckIn);
        request.setNewCheckOut(newCheckOut);
        request.setNewStatus(newStatus);
        request.setReason(reason);
        request.setStatus("PENDING");
        
        return adjustmentRequestRepository.save(request);
    }

    public AttendanceAdjustmentRequest approveAdjustmentRequest(Long requestId, Long approvedById) {
        AttendanceAdjustmentRequest request = findById(requestId);
        Employee approver = employeeRepository.findById(approvedById)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người phê duyệt với ID: " + approvedById));
        
        // Cập nhật attendance
        Attendance attendance = request.getAttendance();
        if (request.getNewCheckIn() != null && !request.getNewCheckIn().isEmpty()) {
            attendance.setCheckIn(LocalTime.parse(request.getNewCheckIn()));
        }
        if (request.getNewCheckOut() != null && !request.getNewCheckOut().isEmpty()) {
            attendance.setCheckOut(LocalTime.parse(request.getNewCheckOut()));
        }
        if (request.getNewStatus() != null && !request.getNewStatus().isEmpty()) {
            attendance.setStatus(request.getNewStatus());
        }
        attendanceRepository.save(attendance);
        
        // Cập nhật request
        request.setStatus("APPROVED");
        request.setApprovedBy(approver);
        request.setApprovedAt(LocalDateTime.now());
        
        return adjustmentRequestRepository.save(request);
    }

    public AttendanceAdjustmentRequest rejectAdjustmentRequest(Long requestId, Long approvedById, String rejectionReason) {
        AttendanceAdjustmentRequest request = findById(requestId);
        Employee approver = employeeRepository.findById(approvedById)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người phê duyệt với ID: " + approvedById));
        
        request.setStatus("REJECTED");
        request.setApprovedBy(approver);
        request.setApprovedAt(LocalDateTime.now());
        request.setRejectionReason(rejectionReason);
        
        return adjustmentRequestRepository.save(request);
    }

    public void delete(Long id) {
        if (!adjustmentRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy yêu cầu điều chỉnh công với ID: " + id);
        }
        adjustmentRequestRepository.deleteById(id);
    }
}

