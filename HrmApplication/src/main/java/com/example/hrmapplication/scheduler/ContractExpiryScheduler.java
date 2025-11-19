package com.example.hrmapplication.scheduler;

import com.example.hrmapplication.entity.Contract;
import com.example.hrmapplication.service.ContractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ContractExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(ContractExpiryScheduler.class);

    @Autowired
    private ContractService contractService;

    /**
     * Kiểm tra hợp đồng sắp hết hạn mỗi ngày lúc 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * ?") // Chạy mỗi ngày lúc 8:00 AM
    public void checkExpiringContracts() {
        try {
            log.info("Bắt đầu kiểm tra hợp đồng sắp hết hạn...");
            
            // Kiểm tra hợp đồng sắp hết hạn trong 7 ngày
            List<Contract> expiringIn7Days = contractService.findExpiringIn7Days();
            if (!expiringIn7Days.isEmpty()) {
                log.warn("Có {} hợp đồng sắp hết hạn trong 7 ngày tới", expiringIn7Days.size());
                for (Contract contract : expiringIn7Days) {
                    log.info("Hợp đồng {} của nhân viên {} sẽ hết hạn vào {}", 
                            contract.getContractNumber(),
                            contract.getEmployee().getFullName(),
                            contract.getEndDate());
                }
            }
            
            // Kiểm tra hợp đồng sắp hết hạn trong 15 ngày
            List<Contract> expiringIn15Days = contractService.findExpiringIn15Days();
            if (!expiringIn15Days.isEmpty()) {
                log.warn("Có {} hợp đồng sắp hết hạn trong 15 ngày tới", expiringIn15Days.size());
            }
            
            // Kiểm tra hợp đồng sắp hết hạn trong 30 ngày
            List<Contract> expiringIn30Days = contractService.findExpiringIn30Days();
            if (!expiringIn30Days.isEmpty()) {
                log.info("Có {} hợp đồng sắp hết hạn trong 30 ngày tới", expiringIn30Days.size());
            }
            
            // Kiểm tra hợp đồng đã hết hạn
            List<Contract> expiredContracts = contractService.findExpiredContracts();
            if (!expiredContracts.isEmpty()) {
                log.error("Có {} hợp đồng đã hết hạn cần xử lý", expiredContracts.size());
                for (Contract contract : expiredContracts) {
                    log.warn("Hợp đồng {} của nhân viên {} đã hết hạn vào {}", 
                            contract.getContractNumber(),
                            contract.getEmployee().getFullName(),
                            contract.getEndDate());
                }
            }
            
            log.info("Hoàn thành kiểm tra hợp đồng sắp hết hạn");
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra hợp đồng sắp hết hạn: {}", e.getMessage(), e);
        }
    }
}

