package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Insurance;
import com.example.hrmapplication.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;

    public List<Insurance> findByEmployeeId(Long employeeId) {
        return insuranceRepository.findByEmployeeId(employeeId);
    }

    public Insurance findById(Long id) {
        return insuranceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bảo hiểm"));
    }

    public Insurance save(Insurance insurance) {
        return insuranceRepository.save(insurance);
    }

    public void delete(Long id) {
        insuranceRepository.deleteById(id);
    }
}