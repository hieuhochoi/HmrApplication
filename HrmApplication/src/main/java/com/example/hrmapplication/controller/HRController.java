package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.*;
import com.example.hrmapplication.repository.ContractRepository;
import com.example.hrmapplication.repository.EmployeeRepository;
import com.example.hrmapplication.service.*;
import com.example.hrmapplication.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
public class HRController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractService contractService;

    @Autowired
    private InsuranceService insuranceService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private OvertimeRequestService overtimeRequestService;

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private com.example.hrmapplication.service.DepartmentService departmentService;

    @Autowired
    private com.example.hrmapplication.service.PositionService positionService;

    @Autowired
    private com.example.hrmapplication.service.WorkHistoryService workHistoryService;

    @Autowired
    private com.example.hrmapplication.service.EmployeeService employeeService;

    @Autowired
    private com.example.hrmapplication.service.ExcelService excelService;

    @Autowired
    private com.example.hrmapplication.service.EmailService emailService;

    // ========== QUẢN LÝ NHÂN VIÊN ==========

    @GetMapping("/employees")
    public String employeeList(@RequestParam(required = false) String search,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) Long departmentId,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "20") int size,
                               Model model) {
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<Employee> employeePage;
            
            if (search != null && !search.trim().isEmpty()) {
                employeePage = employeeRepository.findByFullNameContainingIgnoreCase(search.trim(), pageable);
            } else if (status != null && !status.isEmpty()) {
                employeePage = employeeRepository.findByStatus(status, pageable);
            } else if (departmentId != null) {
                employeePage = employeeRepository.findByCurrentDepartmentId(departmentId, pageable);
            } else {
                employeePage = employeeRepository.findAll(pageable);
            }
            
            model.addAttribute("employees", employeePage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", employeePage.getTotalPages());
            model.addAttribute("totalElements", employeePage.getTotalElements());
            model.addAttribute("search", search);
            model.addAttribute("status", status);
            model.addAttribute("departmentId", departmentId);
            
            // Thêm danh sách phòng ban cho filter
            model.addAttribute("departments", departmentService.findAll());
            
            return "hr/employees/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("employees", List.of());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0L);
            return "hr/employees/list";
        }
    }

    // ========== QUẢN LÝ HỢP ĐỒNG ==========

    @GetMapping("/contracts")
    public String contractList(Model model) {
        try {
            List<Contract> contracts = contractService.findAll();
            model.addAttribute("contracts", contracts != null ? contracts : List.of());
            
            // Thống kê hợp đồng sắp hết hạn
            LocalDate now = LocalDate.now();
            LocalDate in30Days = now.plusDays(30);
            List<Contract> expiringContracts = (contracts != null ? contracts : List.<Contract>of()).stream()
                    .filter(c -> c.getEndDate() != null 
                            && c.getEndDate().isAfter(now) 
                            && c.getEndDate().isBefore(in30Days)
                            && "ACTIVE".equals(c.getStatus()))
                    .toList();
            
            model.addAttribute("expiringContracts", expiringContracts);
            return "hr/contracts/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("contracts", List.of());
            model.addAttribute("expiringContracts", List.of());
            return "hr/contracts/list";
        }
    }

    @GetMapping("/contracts/expiring")
    public String expiringContracts(@RequestParam(defaultValue = "30") int days, Model model) {
        try {
            LocalDate now = LocalDate.now();
            LocalDate targetDate = now.plusDays(days);
            
            List<Contract> allContracts = contractService.findAll();
            List<Contract> expiringContracts = (allContracts != null ? allContracts : List.<Contract>of()).stream()
                    .filter(c -> c.getEndDate() != null 
                            && c.getEndDate().isAfter(now) 
                            && c.getEndDate().isBefore(targetDate)
                            && "ACTIVE".equals(c.getStatus()))
                    .toList();
            
            model.addAttribute("expiringContracts", expiringContracts);
            model.addAttribute("days", days);
            return "hr/contracts/expiring";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("expiringContracts", List.of());
            model.addAttribute("days", days);
            return "hr/contracts/expiring";
        }
    }

    // ========== QUẢN LÝ BẢO HIỂM ==========

    @GetMapping("/insurances")
    public String insuranceList(Model model) {
        try {
            List<Insurance> insurances = insuranceService.findAll();
            model.addAttribute("insurances", insurances != null ? insurances : List.of());
            return "hr/insurances/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("insurances", List.of());
            return "hr/insurances/list";
        }
    }

    // ========== DUYỆT YÊU CẦU ==========

    @GetMapping("/approvals/leave-requests")
    public String allLeaveRequests(Model model) {
        try {
            List<LeaveRequest> leaveRequests = leaveRequestService.findAll();
            model.addAttribute("leaveRequests", leaveRequests != null ? leaveRequests : List.of());
            return "hr/approvals/leave-requests";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("leaveRequests", List.of());
            return "hr/approvals/leave-requests";
        }
    }

    @PostMapping("/approvals/leave-requests/approve/{id}")
    public String approveLeaveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee hr = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (hr == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin HR.");
                return "redirect:/hr/approvals/leave-requests";
            }
            
            leaveRequestService.approveLeaveRequest(id, hr.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Phê duyệt yêu cầu nghỉ phép thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/hr/approvals/leave-requests";
    }

    @PostMapping("/approvals/leave-requests/reject/{id}")
    public String rejectLeaveRequest(@PathVariable Long id,
                                     @RequestParam String rejectionReason,
                                     RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee hr = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (hr == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin HR.");
                return "redirect:/hr/approvals/leave-requests";
            }
            
            leaveRequestService.rejectLeaveRequest(id, hr.getId(), rejectionReason);
            redirectAttributes.addFlashAttribute("successMessage", "Từ chối yêu cầu nghỉ phép thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/hr/approvals/leave-requests";
    }

    @GetMapping("/approvals/overtime-requests")
    public String allOvertimeRequests(Model model) {
        try {
            List<OvertimeRequest> overtimeRequests = overtimeRequestService.findAll();
            model.addAttribute("overtimeRequests", overtimeRequests != null ? overtimeRequests : List.of());
            return "hr/approvals/overtime-requests";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("overtimeRequests", List.of());
            return "hr/approvals/overtime-requests";
        }
    }

    @PostMapping("/approvals/overtime-requests/approve/{id}")
    public String approveOvertimeRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee hr = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (hr == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin HR.");
                return "redirect:/hr/approvals/overtime-requests";
            }
            
            overtimeRequestService.approveOvertimeRequest(id, hr.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Phê duyệt yêu cầu tăng ca thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/hr/approvals/overtime-requests";
    }

    @PostMapping("/approvals/overtime-requests/reject/{id}")
    public String rejectOvertimeRequest(@PathVariable Long id,
                                        @RequestParam String rejectionReason,
                                        RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee hr = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (hr == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin HR.");
                return "redirect:/hr/approvals/overtime-requests";
            }
            
            overtimeRequestService.rejectOvertimeRequest(id, hr.getId(), rejectionReason);
            redirectAttributes.addFlashAttribute("successMessage", "Từ chối yêu cầu tăng ca thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/hr/approvals/overtime-requests";
    }

    // ========== QUẢN LÝ CHẤM CÔNG ==========

    @GetMapping("/attendances")
    public String attendanceList(@RequestParam(required = false) String date, Model model) {
        try {
            List<Attendance> attendances;
            if (date != null && !date.isEmpty()) {
                LocalDate workDate = LocalDate.parse(date);
                attendances = attendanceService.findByWorkDate(workDate);
            } else {
                attendances = attendanceService.findAll();
            }
            model.addAttribute("attendances", attendances != null ? attendances : List.of());
            model.addAttribute("selectedDate", date != null ? date : LocalDate.now().toString());
            return "hr/attendances/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("attendances", List.of());
            model.addAttribute("selectedDate", LocalDate.now().toString());
            return "hr/attendances/list";
        }
    }

    // ========== TÍNH LƯƠNG ==========

    @GetMapping("/salaries")
    public String salaryList(@RequestParam(required = false) Integer month,
                             @RequestParam(required = false) Integer year,
                             Model model) {
        try {
            List<Salary> salaries;
            if (month != null && year != null) {
                salaries = salaryService.findByMonthYear(month, year);
            } else {
                salaries = salaryService.findAll();
            }
            
            // Thống kê
            double totalSalary = salaries.stream()
                    .mapToDouble(s -> s.getTotalSalary() != null ? s.getTotalSalary() : 0.0)
                    .sum();
            double avgSalary = salaries.isEmpty() ? 0.0 : totalSalary / salaries.size();
            long paidCount = salaries.stream()
                    .filter(s -> "PAID".equals(s.getStatus()))
                    .count();
            long pendingCount = salaries.stream()
                    .filter(s -> "PENDING".equals(s.getStatus()))
                    .count();
            
            model.addAttribute("salaries", salaries != null ? salaries : List.of());
            model.addAttribute("selectedMonth", month != null ? month : LocalDate.now().getMonthValue());
            model.addAttribute("selectedYear", year != null ? year : LocalDate.now().getYear());
            model.addAttribute("totalSalary", totalSalary);
            model.addAttribute("avgSalary", avgSalary);
            model.addAttribute("paidCount", paidCount);
            model.addAttribute("pendingCount", pendingCount);
            model.addAttribute("totalCount", salaries.size());
            return "hr/salaries/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("salaries", List.of());
            model.addAttribute("selectedMonth", LocalDate.now().getMonthValue());
            model.addAttribute("selectedYear", LocalDate.now().getYear());
            model.addAttribute("totalSalary", 0.0);
            model.addAttribute("avgSalary", 0.0);
            model.addAttribute("paidCount", 0L);
            model.addAttribute("pendingCount", 0L);
            model.addAttribute("totalCount", 0);
            return "hr/salaries/list";
        }
    }

    // ========== EXPORT BẢNG LƯƠNG ==========

    @GetMapping("/salaries/export/excel")
    public org.springframework.http.ResponseEntity<byte[]> exportSalaryExcel(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        try {
            List<Salary> salaries;
            if (month != null && year != null) {
                salaries = salaryService.findByMonthYear(month, year);
            } else {
                salaries = salaryService.findAll();
            }
            
            byte[] excelBytes = excelService.generateSalaryReportExcel(salaries, month, year);
            
            String filename = "BangLuong_" + (month != null && year != null ? month + "_" + year : "All") + ".xlsx";
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelBytes.length);
            
            return org.springframework.http.ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/salaries/export/pdf")
    public org.springframework.http.ResponseEntity<byte[]> exportSalaryPdf(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        try {
            List<Salary> salaries;
            if (month != null && year != null) {
                salaries = salaryService.findByMonthYear(month, year);
            } else {
                salaries = salaryService.findAll();
            }
            
            // Tạo PDF cho tất cả lương (có thể tạo một file PDF tổng hợp hoặc zip nhiều file)
            // Ở đây tôi sẽ tạo một file PDF đơn giản với danh sách
            byte[] pdfBytes = generateSalaryListPdf(salaries, month, year);
            
            String filename = "BangLuong_" + (month != null && year != null ? month + "_" + year : "All") + ".pdf";
            
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);
            
            return org.springframework.http.ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== TÍNH LƯƠNG TỰ ĐỘNG ==========
    
    @GetMapping("/salaries/calculate")
    public String showCalculateSalaryForm(Model model) {
        model.addAttribute("selectedMonth", LocalDate.now().getMonthValue());
        model.addAttribute("selectedYear", LocalDate.now().getYear());
        return "hr/salaries/calculate";
    }

    @PostMapping("/salaries/calculate")
    public String calculateSalaries(@RequestParam Integer month,
                                    @RequestParam Integer year,
                                    RedirectAttributes redirectAttributes) {
        try {
            if (month == null || year == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn tháng và năm.");
                return "redirect:/hr/salaries/calculate";
            }
            
            int successCount = salaryService.calculateSalariesForAllEmployees(month, year);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Đã tính lương tự động cho " + successCount + " nhân viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/hr/salaries?month=" + month + "&year=" + year;
    }

    // ========== GỬI EMAIL PHIẾU LƯƠNG ==========
    @PostMapping("/salaries/send-emails")
    public String sendPayslips(@RequestParam Integer month,
                               @RequestParam Integer year,
                               RedirectAttributes redirectAttributes) {
        try {
            if (month == null || year == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn tháng và năm để gửi phiếu lương.");
                return "redirect:/hr/salaries";
            }
            List<Salary> salaries = salaryService.findByMonthYear(month, year);
            int sent = 0;
            for (Salary s : salaries) {
                try {
                    emailService.sendPayslip(s);
                    sent++;
                } catch (Exception ex) {
                    // continue
                }
            }
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã gửi " + sent + "/" + salaries.size() + " phiếu lương qua email.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/hr/salaries?month=" + month + "&year=" + year;
    }
    private byte[] generateSalaryListPdf(List<Salary> salaries, Integer month, Integer year) throws Exception {
        com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate());
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        com.lowagie.text.pdf.PdfWriter.getInstance(document, outputStream);
        document.open();

        // Tiêu đề
        com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph(
                "BẢNG LƯƠNG " + (month != null && year != null ? "THÁNG " + month + "/" + year : ""),
                new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD));
        title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Bảng
        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(11);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2});

        // Header
        String[] headers = {"STT", "Họ tên", "Phòng ban", "Chức vụ", "Lương CB", 
                "Phụ cấp", "Thưởng", "Tăng ca", "Tạm ứng", "Khấu trừ", "Tổng lương"};
        com.lowagie.text.Font headerFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.BOLD);
        
        for (String header : headers) {
            com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(header, headerFont));
            cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }

        // Data
        int stt = 1;
        com.lowagie.text.Font normalFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9);
        java.text.NumberFormat formatter = java.text.NumberFormat.getNumberInstance(new java.util.Locale("vi", "VN"));
        
        for (Salary salary : salaries) {
            table.addCell(createCell(String.valueOf(stt++), normalFont));
            table.addCell(createCell(salary.getEmployee().getFullName(), normalFont));
            table.addCell(createCell(salary.getEmployee().getCurrentDepartment() != null ? 
                    salary.getEmployee().getCurrentDepartment().getDepartmentName() : "", normalFont));
            table.addCell(createCell(salary.getEmployee().getCurrentPosition() != null ? 
                    salary.getEmployee().getCurrentPosition().getPositionName() : "", normalFont));
            table.addCell(createCell(formatCurrency(salary.getBaseSalary()), normalFont));
            table.addCell(createCell(formatCurrency(salary.getAllowance()), normalFont));
            table.addCell(createCell(formatCurrency(salary.getBonus()), normalFont));
            table.addCell(createCell(formatCurrency(salary.getOvertime()), normalFont));
            table.addCell(createCell(formatCurrency(salary.getAdvance()), normalFont));
            table.addCell(createCell(formatCurrency(salary.getDeduction()), normalFont));
            table.addCell(createCell(formatCurrency(salary.getTotalSalary()), normalFont));
        }

        document.add(table);
        document.close();
        
        return outputStream.toByteArray();
    }

    private com.lowagie.text.pdf.PdfPCell createCell(String text, com.lowagie.text.Font font) {
        com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    private String formatCurrency(Double amount) {
        if (amount == null) {
            amount = 0.0;
        }
        java.text.NumberFormat formatter = java.text.NumberFormat.getNumberInstance(new java.util.Locale("vi", "VN"));
        return formatter.format(amount);
    }

    // ========== CHUYỂN PHÒNG BAN / THAY ĐỔI CHỨC VỤ ==========

    @GetMapping("/employees/{id}/transfer")
    public String showTransferForm(@PathVariable Long id, Model model) {
        try {
            Employee employee = employeeService.findById(id);
            List<com.example.hrmapplication.entity.Department> departments = departmentService.findAll();
            List<com.example.hrmapplication.entity.Position> positions = positionService.findAll();
            List<Employee> managers = employeeRepository.findAll().stream()
                    .filter(e -> e.getCurrentDepartment() != null)
                    .toList();
            
            model.addAttribute("employee", employee);
            model.addAttribute("departments", departments != null ? departments : List.of());
            model.addAttribute("positions", positions != null ? positions : List.of());
            model.addAttribute("managers", managers != null ? managers : List.of());
            return "hr/employees/transfer";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/hr/employees";
        }
    }

    @PostMapping("/employees/{id}/transfer")
    public String transferEmployee(@PathVariable Long id,
                                   @RequestParam(required = false) Long newDepartmentId,
                                   @RequestParam(required = false) Long newPositionId,
                                   @RequestParam(required = false) Long newManagerId,
                                   @RequestParam String reason,
                                   RedirectAttributes redirectAttributes) {
        try {
            Employee employee = employeeService.findById(id);
            
            // Lưu thông tin cũ vào WorkHistory
            if (employee.getCurrentDepartment() != null || employee.getCurrentPosition() != null) {
                com.example.hrmapplication.entity.WorkHistory oldHistory = new com.example.hrmapplication.entity.WorkHistory();
                oldHistory.setEmployee(employee);
                oldHistory.setDepartment(employee.getCurrentDepartment());
                oldHistory.setPosition(employee.getCurrentPosition());
                oldHistory.setStartDate(employee.getCreatedAt());
                oldHistory.setEndDate(java.time.LocalDate.now());
                oldHistory.setNote("Chuyển phòng ban/chức vụ: " + reason);
                workHistoryService.save(oldHistory);
            }
            
            // Cập nhật thông tin mới
            if (newDepartmentId != null) {
                com.example.hrmapplication.entity.Department newDept = departmentService.findById(newDepartmentId);
                employee.setCurrentDepartment(newDept);
            }
            
            if (newPositionId != null) {
                com.example.hrmapplication.entity.Position newPos = positionService.findById(newPositionId);
                employee.setCurrentPosition(newPos);
            }
            
            if (newManagerId != null) {
                Employee newManager = employeeService.findById(newManagerId);
                employee.setManager(newManager);
            } else if (newDepartmentId != null) {
                // Nếu chuyển phòng ban nhưng không chọn manager, xóa manager cũ
                employee.setManager(null);
            }
            
            employeeService.save(employee);
            
            // Tạo WorkHistory mới
            com.example.hrmapplication.entity.WorkHistory newHistory = new com.example.hrmapplication.entity.WorkHistory();
            newHistory.setEmployee(employee);
            newHistory.setDepartment(employee.getCurrentDepartment());
            newHistory.setPosition(employee.getCurrentPosition());
            newHistory.setStartDate(java.time.LocalDate.now());
            newHistory.setNote("Bắt đầu công tác tại: " + reason);
            workHistoryService.save(newHistory);
            
            redirectAttributes.addFlashAttribute("successMessage", "Chuyển phòng ban/chức vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/hr/employees";
    }

    // ========== GIA HẠN HỢP ĐỒNG ==========

    @GetMapping("/contracts/{id}/renew")
    public String showRenewContractForm(@PathVariable Long id, Model model) {
        try {
            com.example.hrmapplication.entity.Contract contract = contractService.findById(id);
            model.addAttribute("contract", contract);
            return "hr/contracts/renew";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/hr/contracts";
        }
    }

    @PostMapping("/contracts/{id}/renew")
    public String renewContract(@PathVariable Long id,
                               @RequestParam Integer duration,
                               @RequestParam(required = false) Double newSalary,
                               @RequestParam String reason,
                               RedirectAttributes redirectAttributes) {
        try {
            com.example.hrmapplication.entity.Contract oldContract = contractService.findById(id);
            
            // Đóng hợp đồng cũ
            oldContract.setStatus("TERMINATED");
            contractService.save(oldContract);
            
            // Tạo hợp đồng mới
            com.example.hrmapplication.entity.Contract newContract = new com.example.hrmapplication.entity.Contract();
            newContract.setEmployee(oldContract.getEmployee());
            newContract.setContractType(oldContract.getContractType());
            newContract.setSignedDate(java.time.LocalDate.now());
            newContract.setStartDate(oldContract.getEndDate() != null ? 
                    oldContract.getEndDate().plusDays(1) : java.time.LocalDate.now());
            newContract.setDuration(duration);
            newContract.setEndDate(newContract.getStartDate().plusMonths(duration));
            newContract.setSalary(newSalary != null ? newSalary : oldContract.getSalary());
            newContract.setStatus("ACTIVE");
            
            // Tạo số hợp đồng mới
            String newContractNumber = "HD-" + System.currentTimeMillis();
            newContract.setContractNumber(newContractNumber);
            
            contractService.save(newContract);
            redirectAttributes.addFlashAttribute("successMessage", "Gia hạn hợp đồng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/hr/contracts";
    }

    // ========== CHUYỂN LOẠI HỢP ĐỒNG ==========

    @GetMapping("/contracts/{id}/convert")
    public String showConvertContractForm(@PathVariable Long id, Model model) {
        try {
            com.example.hrmapplication.entity.Contract contract = contractService.findById(id);
            model.addAttribute("contract", contract);
            return "hr/contracts/convert";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/hr/contracts";
        }
    }

    @PostMapping("/contracts/{id}/convert")
    public String convertContract(@PathVariable Long id,
                                 @RequestParam String newContractType,
                                 @RequestParam(required = false) Integer duration,
                                 @RequestParam(required = false) Double newSalary,
                                 @RequestParam String reason,
                                 RedirectAttributes redirectAttributes) {
        try {
            com.example.hrmapplication.entity.Contract oldContract = contractService.findById(id);
            
            // Đóng hợp đồng cũ
            oldContract.setStatus("TERMINATED");
            contractService.save(oldContract);
            
            // Tạo hợp đồng mới với loại mới
            com.example.hrmapplication.entity.Contract newContract = new com.example.hrmapplication.entity.Contract();
            newContract.setEmployee(oldContract.getEmployee());
            newContract.setContractType(newContractType);
            newContract.setSignedDate(java.time.LocalDate.now());
            newContract.setStartDate(java.time.LocalDate.now());
            newContract.setDuration(duration != null ? duration : oldContract.getDuration());
            newContract.setEndDate(newContract.getStartDate().plusMonths(newContract.getDuration()));
            newContract.setSalary(newSalary != null ? newSalary : oldContract.getSalary());
            newContract.setStatus("ACTIVE");
            
            // Tạo số hợp đồng mới
            String newContractNumber = "HD-" + System.currentTimeMillis();
            newContract.setContractNumber(newContractNumber);
            
            contractService.save(newContract);
            redirectAttributes.addFlashAttribute("successMessage", "Chuyển loại hợp đồng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/hr/contracts";
    }
}

