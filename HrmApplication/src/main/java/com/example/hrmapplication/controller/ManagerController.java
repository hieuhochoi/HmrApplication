package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.entity.LeaveRequest;
import com.example.hrmapplication.entity.OvertimeRequest;
import com.example.hrmapplication.repository.EmployeeRepository;
import com.example.hrmapplication.service.LeaveRequestService;
import com.example.hrmapplication.service.OvertimeRequestService;
import com.example.hrmapplication.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
public class ManagerController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private OvertimeRequestService overtimeRequestService;

    @Autowired
    private com.example.hrmapplication.service.AttendanceAdjustmentRequestService adjustmentRequestService;

    @Autowired
    private com.example.hrmapplication.service.AttendanceService attendanceService;

    // ========== DUYỆT NGHỈ PHÉP ==========

    @GetMapping("/leave-requests")
    public String pendingLeaveRequests(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);

            if (manager == null) {
                model.addAttribute("error", "Không tìm thấy thông tin trưởng phòng. Vui lòng liên hệ HR để được cập nhật.");
                model.addAttribute("leaveRequests", List.of());
                return "manager/leave-requests/list";
            }

            // Lấy danh sách nhân viên thuộc phòng ban của manager
            List<Employee> subordinates = employeeRepository.findByManagerId(manager.getId());
            List<Long> subordinateIds = (subordinates != null && !subordinates.isEmpty()) 
                    ? subordinates.stream()
                            .filter(e -> e != null && e.getId() != null)
                            .map(Employee::getId)
                            .collect(Collectors.toList())
                    : List.of();

            // Nếu không có nhân viên nào, trả về danh sách rỗng
            if (subordinateIds.isEmpty()) {
                model.addAttribute("leaveRequests", List.of());
                model.addAttribute("manager", manager);
                model.addAttribute("message", "Bạn chưa có nhân viên nào trong phòng ban.");
                return "manager/leave-requests/list";
            }

            // Lấy các yêu cầu nghỉ phép của nhân viên trong phòng ban
            List<LeaveRequest> allPendingRequests = leaveRequestService.findPendingRequests();
            List<LeaveRequest> pendingRequests = (allPendingRequests != null && !allPendingRequests.isEmpty() && !subordinateIds.isEmpty())
                    ? allPendingRequests.stream()
                            .filter(request -> {
                                try {
                                    return request != null && 
                                           request.getEmployee() != null && 
                                           request.getEmployee().getId() != null &&
                                           subordinateIds.contains(request.getEmployee().getId());
                                } catch (Exception e) {
                                    return false;
                                }
                            })
                            .collect(Collectors.toList())
                    : List.of();

            model.addAttribute("leaveRequests", pendingRequests);
            model.addAttribute("manager", manager);
            return "manager/leave-requests/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("leaveRequests", List.of());
            return "manager/leave-requests/list";
        }
    }

    @PostMapping("/leave-requests/approve/{id}")
    public String approveLeaveRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (manager == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin trưởng phòng. Vui lòng liên hệ HR.");
                return "redirect:/manager/leave-requests";
            }
            
            leaveRequestService.approveLeaveRequest(id, manager.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Phê duyệt yêu cầu nghỉ phép thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manager/leave-requests";
    }

    @PostMapping("/leave-requests/reject/{id}")
    public String rejectLeaveRequest(@PathVariable Long id,
                                     @RequestParam String rejectionReason,
                                     RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (manager == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin trưởng phòng. Vui lòng liên hệ HR.");
                return "redirect:/manager/leave-requests";
            }
            
            leaveRequestService.rejectLeaveRequest(id, manager.getId(), rejectionReason);
            redirectAttributes.addFlashAttribute("successMessage", "Từ chối yêu cầu nghỉ phép thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manager/leave-requests";
    }

    // ========== DUYỆT TĂNG CA ==========

    @GetMapping("/overtime-requests")
    public String pendingOvertimeRequests(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);

            if (manager == null) {
                model.addAttribute("error", "Không tìm thấy thông tin trưởng phòng. Vui lòng liên hệ HR để được cập nhật.");
                model.addAttribute("overtimeRequests", List.of());
                return "manager/overtime-requests/list";
            }

            // Lấy danh sách nhân viên thuộc phòng ban của manager
            List<Employee> subordinates = employeeRepository.findByManagerId(manager.getId());
            List<Long> subordinateIds = (subordinates != null && !subordinates.isEmpty()) 
                    ? subordinates.stream()
                            .filter(e -> e != null && e.getId() != null)
                            .map(Employee::getId)
                            .collect(Collectors.toList())
                    : List.of();

            // Nếu không có nhân viên nào, trả về danh sách rỗng
            if (subordinateIds.isEmpty()) {
                model.addAttribute("overtimeRequests", List.of());
                model.addAttribute("manager", manager);
                model.addAttribute("message", "Bạn chưa có nhân viên nào trong phòng ban.");
                return "manager/overtime-requests/list";
            }

            // Lấy các yêu cầu tăng ca của nhân viên trong phòng ban
            List<OvertimeRequest> allPendingRequests = overtimeRequestService.findPendingRequests();
            List<OvertimeRequest> pendingRequests = (allPendingRequests != null && !allPendingRequests.isEmpty() && !subordinateIds.isEmpty())
                    ? allPendingRequests.stream()
                            .filter(request -> {
                                try {
                                    return request != null && 
                                           request.getEmployee() != null && 
                                           request.getEmployee().getId() != null &&
                                           subordinateIds.contains(request.getEmployee().getId());
                                } catch (Exception e) {
                                    return false;
                                }
                            })
                            .collect(Collectors.toList())
                    : List.of();

            model.addAttribute("overtimeRequests", pendingRequests);
            model.addAttribute("manager", manager);
            return "manager/overtime-requests/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("overtimeRequests", List.of());
            return "manager/overtime-requests/list";
        }
    }

    @PostMapping("/overtime-requests/approve/{id}")
    public String approveOvertimeRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (manager == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin trưởng phòng. Vui lòng liên hệ HR.");
                return "redirect:/manager/overtime-requests";
            }
            
            overtimeRequestService.approveOvertimeRequest(id, manager.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Phê duyệt yêu cầu tăng ca thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manager/overtime-requests";
    }

    @PostMapping("/overtime-requests/reject/{id}")
    public String rejectOvertimeRequest(@PathVariable Long id,
                                        @RequestParam String rejectionReason,
                                        RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (manager == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin trưởng phòng. Vui lòng liên hệ HR.");
                return "redirect:/manager/overtime-requests";
            }
            
            overtimeRequestService.rejectOvertimeRequest(id, manager.getId(), rejectionReason);
            redirectAttributes.addFlashAttribute("successMessage", "Từ chối yêu cầu tăng ca thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manager/overtime-requests";
    }

    // ========== BÁO CÁO PHÒNG BAN ==========

    @GetMapping("/reports")
    public String departmentReports(@RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate,
                                   Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);

            if (manager == null) {
                model.addAttribute("error", "Không tìm thấy thông tin trưởng phòng. Vui lòng liên hệ HR để được cập nhật.");
                model.addAttribute("totalEmployees", 0);
                model.addAttribute("activeEmployees", 0);
                model.addAttribute("subordinates", List.of());
                return "manager/reports";
            }

            if (manager.getCurrentDepartment() == null) {
                model.addAttribute("error", "Bạn chưa được gán vào phòng ban nào. Vui lòng liên hệ HR.");
                model.addAttribute("manager", manager);
                model.addAttribute("totalEmployees", 0);
                model.addAttribute("activeEmployees", 0);
                model.addAttribute("subordinates", List.of());
                return "manager/reports";
            }

            List<Employee> subordinates = employeeRepository.findByManagerId(manager.getId());
            long totalEmployees = subordinates != null ? subordinates.size() : 0;
            long activeEmployees = subordinates != null ? subordinates.stream()
                    .filter(e -> e != null && "ACTIVE".equals(e.getStatus()))
                    .count() : 0;

            // Tính thống kê chấm công
            java.time.LocalDate start;
            java.time.LocalDate end;
            if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                start = java.time.LocalDate.parse(startDate);
                end = java.time.LocalDate.parse(endDate);
            } else {
                // Mặc định lấy tháng hiện tại
                java.time.LocalDate now = java.time.LocalDate.now();
                start = now.withDayOfMonth(1);
                end = now.withDayOfMonth(now.lengthOfMonth());
            }

            List<Long> subordinateIds = subordinates != null && !subordinates.isEmpty()
                    ? subordinates.stream()
                            .filter(e -> e != null && e.getId() != null)
                            .map(Employee::getId)
                            .collect(java.util.stream.Collectors.toList())
                    : List.of();

            long lateCount = 0;
            long absentCount = 0;
            long presentCount = 0;
            java.util.Map<String, Long> dailyStats = new java.util.HashMap<>();

            if (!subordinateIds.isEmpty()) {
                lateCount = attendanceService.countByStatus(subordinateIds, "LATE", start, end);
                absentCount = attendanceService.countByStatus(subordinateIds, "ABSENT", start, end);
                presentCount = attendanceService.countByStatus(subordinateIds, "PRESENT", start, end);

                // Thống kê theo ngày (7 ngày gần nhất)
                java.time.LocalDate chartStart = end.minusDays(6);
                if (chartStart.isBefore(start)) {
                    chartStart = start;
                }
                
                for (java.time.LocalDate date = chartStart; !date.isAfter(end); date = date.plusDays(1)) {
                    List<com.example.hrmapplication.entity.Attendance> dayAttendances = 
                            attendanceService.findByEmployeeIdsAndDateRange(subordinateIds, date, date);
                    long dayLate = dayAttendances.stream().filter(a -> "LATE".equals(a.getStatus())).count();
                    long dayAbsent = dayAttendances.stream().filter(a -> "ABSENT".equals(a.getStatus())).count();
                    dailyStats.put(date.toString(), dayLate + dayAbsent);
                }
            }

            model.addAttribute("manager", manager);
            model.addAttribute("department", manager.getCurrentDepartment());
            model.addAttribute("totalEmployees", totalEmployees);
            model.addAttribute("activeEmployees", activeEmployees);
            model.addAttribute("subordinates", subordinates != null ? subordinates : List.of());
            model.addAttribute("lateCount", lateCount);
            model.addAttribute("absentCount", absentCount);
            model.addAttribute("presentCount", presentCount);
            model.addAttribute("startDate", start.toString());
            model.addAttribute("endDate", end.toString());
            model.addAttribute("dailyStats", dailyStats);
            
            return "manager/reports";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("totalEmployees", 0);
            model.addAttribute("activeEmployees", 0);
            model.addAttribute("subordinates", List.of());
            return "manager/reports";
        }
    }

    // ========== DUYỆT YÊU CẦU ĐIỀU CHỈNH CÔNG ==========

    @GetMapping("/attendance-adjustments")
    public String pendingAdjustmentRequests(Model model) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);

            if (manager == null) {
                model.addAttribute("error", "Không tìm thấy thông tin trưởng phòng. Vui lòng liên hệ HR để được cập nhật.");
                model.addAttribute("adjustmentRequests", List.of());
                return "manager/attendance-adjustments/list";
            }

            // Lấy danh sách nhân viên thuộc phòng ban của manager
            List<Employee> subordinates = employeeRepository.findByManagerId(manager.getId());
            List<Long> subordinateIds = (subordinates != null && !subordinates.isEmpty()) 
                    ? subordinates.stream()
                            .filter(e -> e != null && e.getId() != null)
                            .map(Employee::getId)
                            .collect(Collectors.toList())
                    : List.of();

            // Nếu không có nhân viên nào, trả về danh sách rỗng
            if (subordinateIds.isEmpty()) {
                model.addAttribute("adjustmentRequests", List.of());
                model.addAttribute("manager", manager);
                model.addAttribute("message", "Bạn chưa có nhân viên nào trong phòng ban.");
                return "manager/attendance-adjustments/list";
            }

            // Lấy các yêu cầu điều chỉnh công của nhân viên trong phòng ban
            List<com.example.hrmapplication.entity.AttendanceAdjustmentRequest> allPendingRequests = 
                    adjustmentRequestService.findPendingRequests();
            List<com.example.hrmapplication.entity.AttendanceAdjustmentRequest> pendingRequests = 
                    (allPendingRequests != null && !allPendingRequests.isEmpty() && !subordinateIds.isEmpty())
                    ? allPendingRequests.stream()
                            .filter(request -> {
                                try {
                                    return request != null && 
                                           request.getEmployee() != null && 
                                           request.getEmployee().getId() != null &&
                                           subordinateIds.contains(request.getEmployee().getId());
                                } catch (Exception e) {
                                    return false;
                                }
                            })
                            .collect(Collectors.toList())
                    : List.of();

            model.addAttribute("adjustmentRequests", pendingRequests);
            model.addAttribute("manager", manager);
            return "manager/attendance-adjustments/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("adjustmentRequests", List.of());
            return "manager/attendance-adjustments/list";
        }
    }

    @PostMapping("/attendance-adjustments/approve/{id}")
    public String approveAdjustmentRequest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (manager == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin trưởng phòng. Vui lòng liên hệ HR.");
                return "redirect:/manager/attendance-adjustments";
            }
            
            adjustmentRequestService.approveAdjustmentRequest(id, manager.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Phê duyệt yêu cầu điều chỉnh công thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manager/attendance-adjustments";
    }

    @PostMapping("/attendance-adjustments/reject/{id}")
    public String rejectAdjustmentRequest(@PathVariable Long id,
                                         @RequestParam String rejectionReason,
                                         RedirectAttributes redirectAttributes) {
        try {
            String keycloakUserId = SecurityUtil.getCurrentUserId();
            Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId)
                    .orElse(null);
            
            if (manager == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin trưởng phòng. Vui lòng liên hệ HR.");
                return "redirect:/manager/attendance-adjustments";
            }
            
            adjustmentRequestService.rejectAdjustmentRequest(id, manager.getId(), rejectionReason);
            redirectAttributes.addFlashAttribute("successMessage", "Từ chối yêu cầu điều chỉnh công thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/manager/attendance-adjustments";
    }
}

