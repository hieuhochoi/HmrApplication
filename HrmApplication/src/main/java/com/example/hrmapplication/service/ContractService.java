package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Contract;
import com.example.hrmapplication.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractService {

    private final ContractRepository contractRepository;

    public List<Contract> findByEmployeeId(Long employeeId) {
        return contractRepository.findByEmployeeId(employeeId);
    }

    public Contract findById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hợp đồng"));
    }

    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    public void delete(Long id) {
        contractRepository.deleteById(id);
    }
}
