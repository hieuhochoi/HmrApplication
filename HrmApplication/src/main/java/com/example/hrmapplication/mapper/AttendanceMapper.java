package com.example.hrmapplication.mapper;

import com.example.hrmapplication.dto.AttendanceRequest;
import com.example.hrmapplication.dto.AttendanceResponse;
import com.example.hrmapplication.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public Attendance toEntity(AttendanceRequest request) {
        if (request == null) {
            return null;
        }
        Attendance attendance = new Attendance();
        attendance.setId(request.getId());
        applyCommonFields(attendance, request);
        return attendance;
    }

    public AttendanceResponse toResponse(Attendance attendance) {
        if (attendance == null) {
            return null;
        }
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .employeeId(attendance.getEmployee() != null ? attendance.getEmployee().getId() : null)
                .employeeName(attendance.getEmployee() != null ? attendance.getEmployee().getFullName() : null)
                .workDate(attendance.getWorkDate())
                .shift(attendance.getShift())
                .checkIn(attendance.getCheckIn())
                .checkOut(attendance.getCheckOut())
                .workHours(attendance.getWorkHours())
                .overtimeHours(attendance.getOvertimeHours())
                .isLeave(attendance.getIsLeave())
                .leaveReason(attendance.getLeaveReason())
                .status(attendance.getStatus())
                .build();
    }

    public AttendanceRequest toRequest(Attendance attendance) {
        if (attendance == null) {
            return null;
        }
        AttendanceRequest request = new AttendanceRequest();
        request.setId(attendance.getId());
        request.setEmployeeId(attendance.getEmployee() != null ? attendance.getEmployee().getId() : null);
        request.setWorkDate(attendance.getWorkDate());
        request.setShift(attendance.getShift());
        request.setCheckIn(attendance.getCheckIn());
        request.setCheckOut(attendance.getCheckOut());
        request.setWorkHours(attendance.getWorkHours());
        request.setOvertimeHours(attendance.getOvertimeHours());
        request.setIsLeave(attendance.getIsLeave());
        request.setLeaveReason(attendance.getLeaveReason());
        request.setStatus(attendance.getStatus());
        return request;
    }

    public void updateEntity(Attendance entity, AttendanceRequest request) {
        if (entity == null || request == null) {
            return;
        }
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(Attendance target, AttendanceRequest request) {
        target.setWorkDate(request.getWorkDate());
        target.setShift(request.getShift());
        target.setCheckIn(request.getCheckIn());
        target.setCheckOut(request.getCheckOut());
        target.setWorkHours(request.getWorkHours());
        target.setOvertimeHours(request.getOvertimeHours());
        target.setIsLeave(request.getIsLeave());
        target.setLeaveReason(request.getLeaveReason());
        target.setStatus(request.getStatus());
    }
}

