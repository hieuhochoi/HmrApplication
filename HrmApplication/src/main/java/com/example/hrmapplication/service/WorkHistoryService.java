package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.WorkHistory;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.WorkHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkHistoryService {

    private final WorkHistoryRepository workHistoryRepository;

    public List<WorkHistory> findByEmployeeId(Long employeeId) {
        return workHistoryRepository.findByEmployeeId(employeeId);
    }

    public WorkHistory findById(Long id) {
        return workHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quá trình công tác với ID: " + id));
    }

    public WorkHistory save(WorkHistory workHistory) {
        return workHistoryRepository.save(workHistory);
    }

    public void delete(Long id) {
        workHistoryRepository.deleteById(id);
    }
}