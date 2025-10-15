package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Education;
import com.example.hrmapplication.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EducationService {

    private final EducationRepository educationRepository;

    public List<Education> findByEmployeeId(Long employeeId) {
        return educationRepository.findByEmployeeId(employeeId);
    }

    public Education findById(Long id) {
        return educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy trình độ"));
    }

    public Education save(Education education) {
        return educationRepository.save(education);
    }

    public void delete(Long id) {
        educationRepository.deleteById(id);
    }
}
