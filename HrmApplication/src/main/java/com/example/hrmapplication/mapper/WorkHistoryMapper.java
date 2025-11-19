package com.example.hrmapplication.mapper;

import com.example.hrmapplication.dto.WorkHistoryRequest;
import com.example.hrmapplication.dto.WorkHistoryResponse;
import com.example.hrmapplication.entity.WorkHistory;
import org.springframework.stereotype.Component;

@Component
public class WorkHistoryMapper {

    public WorkHistory toEntity(WorkHistoryRequest request) {
        if (request == null) {
            return null;
        }
        WorkHistory workHistory = new WorkHistory();
        workHistory.setId(request.getId());
        applyCommonFields(workHistory, request);
        return workHistory;
    }

    public WorkHistoryResponse toResponse(WorkHistory workHistory) {
        if (workHistory == null) {
            return null;
        }
        return WorkHistoryResponse.builder()
                .id(workHistory.getId())
                .employeeId(workHistory.getEmployee() != null ? workHistory.getEmployee().getId() : null)
                .employeeName(workHistory.getEmployee() != null ? workHistory.getEmployee().getFullName() : null)
                .departmentId(workHistory.getDepartment() != null ? workHistory.getDepartment().getId() : null)
                .departmentName(workHistory.getDepartment() != null ? workHistory.getDepartment().getDepartmentName() : null)
                .positionId(workHistory.getPosition() != null ? workHistory.getPosition().getId() : null)
                .positionName(workHistory.getPosition() != null ? workHistory.getPosition().getPositionName() : null)
                .startDate(workHistory.getStartDate())
                .endDate(workHistory.getEndDate())
                .leaveType(workHistory.getLeaveType())
                .rewardType(workHistory.getRewardType())
                .disciplineType(workHistory.getDisciplineType())
                .note(workHistory.getNote())
                .build();
    }

    public WorkHistoryRequest toRequest(WorkHistory workHistory) {
        if (workHistory == null) {
            return null;
        }
        WorkHistoryRequest request = new WorkHistoryRequest();
        request.setId(workHistory.getId());
        request.setEmployeeId(workHistory.getEmployee() != null ? workHistory.getEmployee().getId() : null);
        request.setDepartmentId(workHistory.getDepartment() != null ? workHistory.getDepartment().getId() : null);
        request.setPositionId(workHistory.getPosition() != null ? workHistory.getPosition().getId() : null);
        request.setStartDate(workHistory.getStartDate());
        request.setEndDate(workHistory.getEndDate());
        request.setLeaveType(workHistory.getLeaveType());
        request.setRewardType(workHistory.getRewardType());
        request.setDisciplineType(workHistory.getDisciplineType());
        request.setNote(workHistory.getNote());
        return request;
    }

    public void updateEntity(WorkHistory entity, WorkHistoryRequest request) {
        if (entity == null || request == null) {
            return;
        }
        applyCommonFields(entity, request);
    }

    private void applyCommonFields(WorkHistory target, WorkHistoryRequest request) {
        target.setStartDate(request.getStartDate());
        target.setEndDate(request.getEndDate());
        target.setLeaveType(request.getLeaveType());
        target.setRewardType(request.getRewardType());
        target.setDisciplineType(request.getDisciplineType());
        target.setNote(request.getNote());
    }
}

