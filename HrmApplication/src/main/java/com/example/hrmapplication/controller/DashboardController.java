package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.repository.EmployeeRepository;
import com.example.hrmapplication.service.ContractService;
import com.example.hrmapplication.service.LeaveRequestService;
import com.example.hrmapplication.service.OvertimeRequestService;
import com.example.hrmapplication.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ContractService contractService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private OvertimeRequestService overtimeRequestService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal == null) {
            return "redirect:/oauth2/authorization/keycloak";
        }

        List<String> roles = SecurityUtil.getCurrentUserRoles();
        String username = SecurityUtil.getCurrentUsername();
        String userId = SecurityUtil.getCurrentUserId();

        // Đảm bảo roles không null
        if (roles == null) {
            roles = List.of();
        }

        model.addAttribute("username", username);
        model.addAttribute("email", principal.getEmail());
        model.addAttribute("fullName", principal.getFullName());
        model.addAttribute("roles", roles);
        model.addAttribute("userId", userId);

        // Tìm employee tương ứng với Keycloak user
        Employee currentEmployee = employeeRepository.findByKeycloakUserId(userId).orElse(null);
        // Nếu chưa liên kết, thử tự động liên kết theo email
        if (currentEmployee == null && principal.getEmail() != null) {
            try {
                employeeRepository.findByEmail(principal.getEmail())
                        .ifPresent(emp -> {
                            if (emp.getKeycloakUserId() == null || emp.getKeycloakUserId().isBlank()) {
                                emp.setKeycloakUserId(userId);
                                // Lưu liên kết
                                employeeRepository.save(emp);
                            }
                        });
                // Tìm lại sau khi liên kết
                currentEmployee = employeeRepository.findByKeycloakUserId(userId).orElse(null);
            } catch (Exception ignored) {
                // Bỏ qua lỗi auto-link để không chặn luồng đăng nhập
            }
        }
        model.addAttribute("currentEmployee", currentEmployee);

        // Phân luồng theo vai trò
        if (roles.contains("ADMIN")) {
            return redirectToAdminDashboard(model);
        } else if (roles.contains("HR")) {
            return redirectToHRDashboard(model);
        } else if (roles.contains("MANAGER")) {
            return redirectToManagerDashboard(model, currentEmployee);
        } else {
            return redirectToEmployeeDashboard(model, currentEmployee);
        }
    }

    private String redirectToAdminDashboard(Model model) {
        // Thống kê tổng quan cho Admin
        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.countByStatus("ACTIVE");
        long inactiveEmployees = employeeRepository.countByStatus("INACTIVE");
        
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("activeEmployees", activeEmployees);
        model.addAttribute("inactiveEmployees", inactiveEmployees);
        model.addAttribute("dashboardType", "admin");
        
        return "dashboard/admin";
    }

    private String redirectToHRDashboard(Model model) {
        // Thống kê cho HR
        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.countByStatus("ACTIVE");
        
        // Đếm hợp đồng sắp hết hạn (30 ngày)
        long expiringContracts = contractService.countExpiringContracts(30);
        
        // Đếm yêu cầu chờ duyệt
        long pendingLeaveRequests = leaveRequestService.findPendingRequests().size();
        long pendingOvertimeRequests = overtimeRequestService.findPendingRequests().size();
        long totalPendingRequests = pendingLeaveRequests + pendingOvertimeRequests;
        
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("activeEmployees", activeEmployees);
        model.addAttribute("expiringContracts", expiringContracts);
        model.addAttribute("totalPendingRequests", totalPendingRequests);
        model.addAttribute("dashboardType", "hr");
        
        return "dashboard/hr";
    }

    private String redirectToManagerDashboard(Model model, Employee currentEmployee) {
        try {
            if (currentEmployee == null) {
                // Nếu user có vai trò HR nhưng không có hồ sơ Employee, điều hướng về dashboard HR
                try {
                    List<String> roles = SecurityUtil.getCurrentUserRoles();
                    if (roles != null && roles.contains("HR")) {
                        return redirectToHRDashboard(model);
                    }
                } catch (Exception ignored) {
                    // Bỏ qua và hiển thị thông báo mặc định bên dưới
                }

                model.addAttribute("error", "Không tìm thấy thông tin nhân viên. Vui lòng liên hệ HR để được cập nhật.");
                model.addAttribute("departmentEmployees", 0);
                model.addAttribute("dashboardType", "manager");
                return "dashboard/manager";
            }

            if (currentEmployee.getCurrentDepartment() == null) {
                model.addAttribute("error", "Bạn chưa được gán vào phòng ban nào. Vui lòng liên hệ HR.");
                model.addAttribute("departmentEmployees", 0);
                model.addAttribute("dashboardType", "manager");
                return "dashboard/manager";
            }
            
            // Thống kê phòng ban - đếm nhân viên có manager là currentEmployee
            long departmentEmployees = 0;
            try {
                List<Employee> subordinates = employeeRepository.findByManagerId(currentEmployee.getId());
                departmentEmployees = subordinates != null ? subordinates.size() : 0;
            } catch (Exception e) {
                // Nếu lỗi, để giá trị 0
                departmentEmployees = 0;
            }
            
            model.addAttribute("departmentEmployees", departmentEmployees);
            model.addAttribute("department", currentEmployee.getCurrentDepartment());
            model.addAttribute("dashboardType", "manager");
            
            return "dashboard/manager";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("departmentEmployees", 0);
            model.addAttribute("dashboardType", "manager");
            return "dashboard/manager";
        }
    }

    private String redirectToEmployeeDashboard(Model model, Employee currentEmployee) {
        model.addAttribute("employee", currentEmployee);
        model.addAttribute("dashboardType", "employee");
        
        return "dashboard/employee";
    }
}

