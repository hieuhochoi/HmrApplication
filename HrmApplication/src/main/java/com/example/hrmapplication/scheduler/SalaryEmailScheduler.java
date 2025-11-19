package com.example.hrmapplication.scheduler;

import com.example.hrmapplication.entity.Salary;
import com.example.hrmapplication.service.EmailService;
import com.example.hrmapplication.service.SalaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@ConditionalOnProperty(prefix = "hrm.salary.payslip.auto-send", name = "enabled", havingValue = "true")
public class SalaryEmailScheduler {

    private static final Logger log = LoggerFactory.getLogger(SalaryEmailScheduler.class);

    private final SalaryService salaryService;
    private final EmailService emailService;

    @Value("${hrm.salary.payslip.auto-send.cron:0 0 8 1 * ?}")
    private String cron; // chỉ để hiện thông tin log

    public SalaryEmailScheduler(SalaryService salaryService, EmailService emailService) {
        this.salaryService = salaryService;
        this.emailService = emailService;
    }

    // Mặc định: ngày 1 hàng tháng lúc 08:00, gửi phiếu lương tháng trước
    @Scheduled(cron = "${hrm.salary.payslip.auto-send.cron:0 0 8 1 * ?}")
    public void autoSendLastMonthPayslips() {
        try {
            LocalDate now = LocalDate.now();
            LocalDate lastMonth = now.minusMonths(1);
            int month = lastMonth.getMonthValue();
            int year = lastMonth.getYear();

            log.info("Auto send payslips - schedule [{}] for {}/{}", cron, month, year);

            List<Salary> salaries = salaryService.findByMonthYear(month, year);
            int sent = 0;
            for (Salary s : salaries) {
                try {
                    emailService.sendPayslip(s);
                    sent++;
                } catch (Exception ex) {
                    log.warn("Gửi phiếu lương thất bại cho employeeId={}, salaryId={}: {}", 
                            s.getEmployee() != null ? s.getEmployee().getId() : null,
                            s.getId(), ex.getMessage());
                }
            }
            log.info("Auto-sent payslips: {}/{} for {}/{}", sent, salaries.size(), month, year);
        } catch (Exception e) {
            log.error("Lỗi scheduler gửi payslip: {}", e.getMessage(), e);
        }
    }
}


