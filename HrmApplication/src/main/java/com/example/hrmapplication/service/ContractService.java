package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Contract;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractService {

    private final ContractRepository contractRepository;

    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    public List<Contract> findByEmployeeId(Long employeeId) {
        return contractRepository.findByEmployeeId(employeeId);
    }

    public Contract findById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hợp đồng với ID: " + id));
    }

    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    public void delete(Long id) {
        contractRepository.deleteById(id);
    }

    /**
     * Tìm các hợp đồng sắp hết hạn trong số ngày chỉ định
     */
    public List<Contract> findExpiringContracts(int days) {
        LocalDate now = LocalDate.now();
        LocalDate targetDate = now.plusDays(days);
        
        return contractRepository.findAll().stream()
                .filter(c -> c.getEndDate() != null 
                        && c.getEndDate().isAfter(now) 
                        && c.getEndDate().isBefore(targetDate)
                        && "ACTIVE".equals(c.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Tìm các hợp đồng sắp hết hạn trong 7 ngày
     */
    public List<Contract> findExpiringIn7Days() {
        return findExpiringContracts(7);
    }

    /**
     * Tìm các hợp đồng sắp hết hạn trong 15 ngày
     */
    public List<Contract> findExpiringIn15Days() {
        return findExpiringContracts(15);
    }

    /**
     * Tìm các hợp đồng sắp hết hạn trong 30 ngày
     */
    public List<Contract> findExpiringIn30Days() {
        return findExpiringContracts(30);
    }

    /**
     * Tìm các hợp đồng đã hết hạn
     */
    public List<Contract> findExpiredContracts() {
        LocalDate now = LocalDate.now();
        
        return contractRepository.findAll().stream()
                .filter(c -> c.getEndDate() != null 
                        && c.getEndDate().isBefore(now)
                        && "ACTIVE".equals(c.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Đếm số hợp đồng sắp hết hạn
     */
    public long countExpiringContracts(int days) {
        return findExpiringContracts(days).size();
    }
}
