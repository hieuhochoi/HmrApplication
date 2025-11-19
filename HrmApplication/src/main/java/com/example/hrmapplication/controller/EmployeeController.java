package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.entity.LeaveRequest;
import com.example.hrmapplication.entity.OvertimeRequest;
import com.example.hrmapplication.entity.Salary;
import com.example.hrmapplication.repository.EmployeeRepository;
import com.example.hrmapplication.service.LeaveRequestService;
import com.example.hrmapplication.service.OvertimeRequestService;
import com.example.hrmapplication.service.PdfService;
import com.example.hrmapplication.service.SalaryService;
import com.example.hrmapplication.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employee")
@PreAuthorize("isAuthenticated()")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private OvertimeRequestService overtimeRequestService;

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private com.example.hrmapplication.service.AttendanceService attendanceService;

    @Autowired
    private com.example.hrmapplication.service.AttendanceAdjustmentRequestService adjustmentRequestService;

    // ========== XEM HỒ SƠ CÁ NHÂN ==========

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                return "employee/profile";
            }
            
            model.addAttribute("employee", employee);
            return "employee/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "employee/profile";
        }
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                return "redirect:/employee/profile";
            }
            
            model.addAttribute("employee", employee);
            return "employee/profile-edit";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/employee/profile";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam(required = false) String phone,
                                @RequestParam(required = false) String currentAddress,
                                RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin nhân viên.");
                return "redirect:/employee/profile";
            }
            
            // Chỉ cho phép cập nhật phone và address
            if (phone != null && !phone.trim().isEmpty()) {
                // Validate phone format
                if (phone.matches("^[0-9]{10}$")) {
                    employee.setPhone(phone.trim());
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Số điện thoại phải có 10 chữ số.");
                    return "redirect:/employee/profile/edit";
                }
            }
            
            if (currentAddress != null) {
                employee.setCurrentAddress(currentAddress.trim());
            }
            
            employeeRepository.save(employee);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/employee/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/employee/profile/edit";
        }
    }

    // ========== QUẢN LÝ NGHỈ PHÉP ==========

    @GetMapping("/leave-requests")
    public String myLeaveRequests(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                model.addAttribute("leaveRequests", List.of());
                return "employee/leave-requests/list";
            }
            
            List<LeaveRequest> leaveRequests = leaveRequestService.findByEmployeeId(employee.getId());
            
            // Tính số ngày phép còn lại
            Double totalAnnualLeaveDays = leaveRequestService.getTotalAnnualLeaveDays(employee.getId());
            Double usedAnnualLeaveDays = leaveRequestService.getUsedAnnualLeaveDays(employee.getId());
            Double remainingAnnualLeaveDays = leaveRequestService.getRemainingAnnualLeaveDays(employee.getId());
            
            model.addAttribute("leaveRequests", leaveRequests != null ? leaveRequests : List.of());
            model.addAttribute("employee", employee);
            model.addAttribute("totalAnnualLeaveDays", totalAnnualLeaveDays);
            model.addAttribute("usedAnnualLeaveDays", usedAnnualLeaveDays);
            model.addAttribute("remainingAnnualLeaveDays", remainingAnnualLeaveDays);
            return "employee/leave-requests/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("leaveRequests", List.of());
            return "employee/leave-requests/list";
        }
    }

    @GetMapping("/leave-requests/form")
    public String showLeaveRequestForm(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                return "employee/leave-requests/form";
            }
            
            // Tính số ngày phép còn lại
            Double remainingAnnualLeaveDays = leaveRequestService.getRemainingAnnualLeaveDays(employee.getId());
            
            model.addAttribute("leaveRequest", new LeaveRequest());
            model.addAttribute("employee", employee);
            model.addAttribute("remainingAnnualLeaveDays", remainingAnnualLeaveDays);
            return "employee/leave-requests/form";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "employee/leave-requests/form";
        }
    }

    @PostMapping("/leave-requests/save")
    public String createLeaveRequest(@Valid @ModelAttribute LeaveRequest leaveRequest,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "employee/leave-requests/form";
        }

        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR.");
                return "redirect:/employee/leave-requests/form";
            }
            
            leaveRequestService.createLeaveRequest(employee.getId(), leaveRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Gửi yêu cầu nghỉ phép thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/employee/leave-requests/form";
        }

        return "redirect:/employee/leave-requests";
    }

    // ========== QUẢN LÝ TĂNG CA ==========

    @GetMapping("/overtime-requests")
    public String myOvertimeRequests(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                model.addAttribute("overtimeRequests", List.of());
                return "employee/overtime-requests/list";
            }
            
            List<OvertimeRequest> overtimeRequests = overtimeRequestService.findByEmployeeId(employee.getId());
            model.addAttribute("overtimeRequests", overtimeRequests != null ? overtimeRequests : List.of());
            model.addAttribute("employee", employee);
            return "employee/overtime-requests/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("overtimeRequests", List.of());
            return "employee/overtime-requests/list";
        }
    }

    @GetMapping("/overtime-requests/form")
    public String showOvertimeRequestForm(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                return "employee/overtime-requests/form";
            }
            
            model.addAttribute("overtimeRequest", new OvertimeRequest());
            model.addAttribute("employee", employee);
            return "employee/overtime-requests/form";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "employee/overtime-requests/form";
        }
    }

    @PostMapping("/overtime-requests/save")
    public String createOvertimeRequest(@Valid @ModelAttribute OvertimeRequest overtimeRequest,
                                        BindingResult result,
                                        RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "employee/overtime-requests/form";
        }

        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR.");
                return "redirect:/employee/overtime-requests/form";
            }
            
            overtimeRequestService.createOvertimeRequest(employee.getId(), overtimeRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Gửi yêu cầu tăng ca thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/employee/overtime-requests/form";
        }

        return "redirect:/employee/overtime-requests";
    }

    // ========== XEM LƯƠNG ==========

    @GetMapping("/salaries")
    public String mySalaries(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                model.addAttribute("salaries", List.of());
                return "employee/salaries/list";
            }
            
            List<Salary> salaries = salaryService.findByEmployeeId(employee.getId());
            model.addAttribute("salaries", salaries != null ? salaries : List.of());
            model.addAttribute("employee", employee);
            return "employee/salaries/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("salaries", List.of());
            return "employee/salaries/list";
        }
    }

    @GetMapping("/salaries/{id}")
    public String viewSalaryDetail(@PathVariable Long id, Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR.");
                return "employee/salaries/detail";
            }
            
            Salary salary = salaryService.findById(id);
            if (salary == null) {
                model.addAttribute("error", "Không tìm thấy phiếu lương này.");
                return "employee/salaries/detail";
            }
            
            // Kiểm tra quyền xem lương
            if (salary.getEmployee() == null || salary.getEmployee().getId() == null ||
                !salary.getEmployee().getId().equals(employee.getId())) {
                model.addAttribute("error", "Bạn không có quyền xem phiếu lương này");
                return "employee/salaries/detail";
            }
            
            model.addAttribute("salary", salary);
            return "employee/salaries/detail";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "employee/salaries/detail";
        }
    }

    @GetMapping("/salaries/{id}/download")
    public ResponseEntity<byte[]> downloadPayslipPdf(@PathVariable Long id) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }
            
            Salary salary = salaryService.findById(id);
            if (salary == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Kiểm tra quyền xem lương
            if (salary.getEmployee() == null || salary.getEmployee().getId() == null ||
                !salary.getEmployee().getId().equals(employee.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            byte[] pdfBytes = pdfService.generatePayslipPdf(salary);
            
            String filename = "Payslip_" + salary.getEmployee().getFullName().replaceAll("\\s+", "_") 
                    + "_" + salary.getMonth() + "_" + salary.getYear() + ".pdf";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ========== XEM BẢNG CÔNG CÁ NHÂN ==========

    @GetMapping("/attendances")
    public String myAttendances(@RequestParam(required = false) String startDate,
                                @RequestParam(required = false) String endDate,
                                Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                model.addAttribute("attendances", List.of());
                return "employee/attendances/list";
            }
            
            List<com.example.hrmapplication.entity.Attendance> attendances;
            if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                java.time.LocalDate start = java.time.LocalDate.parse(startDate);
                java.time.LocalDate end = java.time.LocalDate.parse(endDate);
                attendances = attendanceService.findByDateRange(employee.getId(), start, end);
            } else {
                // Mặc định lấy tháng hiện tại
                java.time.LocalDate now = java.time.LocalDate.now();
                java.time.LocalDate start = now.withDayOfMonth(1);
                java.time.LocalDate end = now.withDayOfMonth(now.lengthOfMonth());
                attendances = attendanceService.findByDateRange(employee.getId(), start, end);
            }
            
            model.addAttribute("attendances", attendances != null ? attendances : List.of());
            model.addAttribute("employee", employee);
            model.addAttribute("startDate", startDate != null ? startDate : java.time.LocalDate.now().withDayOfMonth(1).toString());
            model.addAttribute("endDate", endDate != null ? endDate : java.time.LocalDate.now().withDayOfMonth(java.time.LocalDate.now().lengthOfMonth()).toString());
            return "employee/attendances/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("attendances", List.of());
            return "employee/attendances/list";
        }
    }

    // ========== YÊU CẦU ĐIỀU CHỈNH CÔNG ==========

    @GetMapping("/attendance-adjustments")
    public String myAdjustmentRequests(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                model.addAttribute("adjustmentRequests", List.of());
                return "employee/attendance-adjustments/list";
            }
            
            List<com.example.hrmapplication.entity.AttendanceAdjustmentRequest> requests = 
                    adjustmentRequestService.findByEmployeeId(employee.getId());
            model.addAttribute("adjustmentRequests", requests != null ? requests : List.of());
            model.addAttribute("employee", employee);
            return "employee/attendance-adjustments/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("adjustmentRequests", List.of());
            return "employee/attendance-adjustments/list";
        }
    }

    @GetMapping("/attendance-adjustments/form")
    public String showAdjustmentRequestForm(@RequestParam Long attendanceId, Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                return "redirect:/employee/attendances";
            }
            
            com.example.hrmapplication.entity.Attendance attendance = attendanceService.findById(attendanceId);
            
            // Kiểm tra quyền
            if (attendance.getEmployee() == null || attendance.getEmployee().getId() == null ||
                !attendance.getEmployee().getId().equals(employee.getId())) {
                model.addAttribute("error", "Bạn không có quyền điều chỉnh bản ghi chấm công này.");
                return "redirect:/employee/attendances";
            }
            
            model.addAttribute("attendance", attendance);
            model.addAttribute("employee", employee);
            return "employee/attendance-adjustments/form";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/employee/attendances";
        }
    }

    @PostMapping("/attendance-adjustments/save")
    public String createAdjustmentRequest(@RequestParam Long attendanceId,
                                         @RequestParam(required = false) String newCheckIn,
                                         @RequestParam(required = false) String newCheckOut,
                                         @RequestParam(required = false) String newStatus,
                                         @RequestParam String reason,
                                         RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (employee == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR.");
                return "redirect:/employee/attendances";
            }
            
            adjustmentRequestService.createAdjustmentRequest(
                    employee.getId(), attendanceId, newCheckIn, newCheckOut, newStatus, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Gửi yêu cầu điều chỉnh công thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/employee/attendance-adjustments/form?attendanceId=" + attendanceId;
        }

        return "redirect:/employee/attendance-adjustments";
    }
}
