package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.*;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;
    private final OvertimeRequestRepository overtimeRequestRepository;
    private final ContractRepository contractRepository;

    public List<Salary> findAll() {
        return salaryRepository.findAll();
    }

    public List<Salary> findByEmployeeId(Long employeeId) {
        return salaryRepository.findByEmployeeId(employeeId);
    }

    public List<Salary> findByMonthYear(Integer month, Integer year) {
        return salaryRepository.findByMonthAndYear(month, year);
    }

    public Salary findById(Long id) {
        return salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bảng lương"));
    }

    public Salary save(Salary salary) {
        return salaryRepository.save(salary);
    }

    public void delete(Long id) {
        salaryRepository.deleteById(id);
    }

    /**
     * Tính lương tự động cho một nhân viên trong tháng/năm cụ thể
     * Dựa trên: lương cơ bản từ hợp đồng, chấm công, tăng ca đã được phê duyệt
     */
    public Salary calculateSalaryForEmployee(Long employeeId, Integer month, Integer year) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        // Kiểm tra xem đã có lương chưa
        Optional<Salary> existingSalary = salaryRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
        if (existingSalary.isPresent()) {
            throw new IllegalStateException("Đã tồn tại bảng lương cho nhân viên này trong tháng/năm này");
        }

        // Lấy lương cơ bản từ hợp đồng hiện tại
        Double baseSalary = getBaseSalaryFromContract(employeeId);
        if (baseSalary == null || baseSalary <= 0) {
            throw new IllegalStateException("Nhân viên chưa có hợp đồng hoặc lương cơ bản");
        }

        // Tính toán ngày bắt đầu và kết thúc của tháng
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // Tính số ngày làm việc (chấm công PRESENT)
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndWorkDateBetween(employeeId, startDate, endDate);
        long workingDays = attendances.stream()
                .filter(a -> "PRESENT".equals(a.getStatus()))
                .count();

        // Tính số ngày nghỉ (ABSENT)
        long absentDays = attendances.stream()
                .filter(a -> "ABSENT".equals(a.getStatus()))
                .count();

        // Tính lương theo ngày làm việc (giả sử 1 tháng = 22 ngày làm việc)
        int standardWorkingDays = 22;
        double dailySalary = baseSalary / standardWorkingDays;
        double actualBaseSalary = workingDays * dailySalary;

        // Tính tăng ca (chỉ tính các yêu cầu đã được phê duyệt trong tháng)
        List<OvertimeRequest> overtimeRequests = overtimeRequestRepository.findByEmployeeId(employeeId).stream()
                .filter(ot -> ot.getOvertimeDate() != null
                        && ot.getOvertimeDate().getMonthValue() == month
                        && ot.getOvertimeDate().getYear() == year
                        && "APPROVED".equals(ot.getStatus()))
                .toList();

        double totalOvertimeHours = overtimeRequests.stream()
                .mapToDouble(ot -> ot.getTotalHours() != null ? ot.getTotalHours() : 0.0)
                .sum();

        // Tính tiền tăng ca (1.5 lần lương giờ bình thường)
        double hourlySalary = dailySalary / 8.0; // 8 giờ/ngày
        double overtimeAmount = totalOvertimeHours * hourlySalary * 1.5;

        // Tạo bảng lương
        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setMonth(month);
        salary.setYear(year);
        salary.setBaseSalary(actualBaseSalary);
        salary.setAllowance(0.0); // Có thể tính từ phụ cấp của nhân viên
        salary.setBonus(0.0); // Có thể tính từ KPI hoặc thưởng
        salary.setOvertime(overtimeAmount);
        salary.setAdvance(0.0);
        salary.setDeduction(absentDays * dailySalary); // Trừ lương cho ngày nghỉ không phép
        salary.setStatus("PENDING");

        return salaryRepository.save(salary);
    }

    /**
     * Tính lương tự động cho tất cả nhân viên trong tháng/năm
     */
    public int calculateSalariesForAllEmployees(Integer month, Integer year) {
        List<Employee> activeEmployees = employeeRepository.findAll().stream()
                .filter(e -> "ACTIVE".equals(e.getStatus()))
                .toList();

        int successCount = 0;
        for (Employee employee : activeEmployees) {
            try {
                calculateSalaryForEmployee(employee.getId(), month, year);
                successCount++;
            } catch (Exception e) {
                // Log lỗi nhưng tiếp tục với nhân viên khác
                System.err.println("Lỗi tính lương cho nhân viên " + employee.getId() + ": " + e.getMessage());
            }
        }
        return successCount;
    }

    /**
     * Lấy lương cơ bản từ hợp đồng hiện tại của nhân viên
     */
    private Double getBaseSalaryFromContract(Long employeeId) {
        List<Contract> contracts = contractRepository.findByEmployeeId(employeeId);
        LocalDate now = LocalDate.now();
        
        return contracts.stream()
                .filter(c -> "ACTIVE".equals(c.getStatus())
                        && (c.getStartDate() == null || !c.getStartDate().isAfter(now))
                        && (c.getEndDate() == null || !c.getEndDate().isBefore(now)))
                .findFirst()
                .map(Contract::getSalary)
                .orElse(null);
    }
}