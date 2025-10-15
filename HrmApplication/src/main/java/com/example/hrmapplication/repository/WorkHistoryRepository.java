package com.example.hrmapplication.repository;

import com.example.hrmapplication.entity.WorkHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkHistoryRepository extends JpaRepository<WorkHistory, Long> {
    List<WorkHistory> findByEmployeeId(Long employeeId);
}