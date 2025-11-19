package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.MasterData;
import com.example.hrmapplication.entity.SystemConfig;
import com.example.hrmapplication.service.MasterDataService;
import com.example.hrmapplication.service.SystemConfigService;
import com.example.hrmapplication.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private MasterDataService masterDataService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private com.example.hrmapplication.service.AuditLogService auditLogService;

    @Autowired
    private com.example.hrmapplication.service.ContractService contractService;

    @Autowired
    private com.example.hrmapplication.service.KeycloakUserService keycloakUserService;

    @Autowired
    private com.example.hrmapplication.service.KeycloakSyncService keycloakSyncService;

    @Autowired
    private com.example.hrmapplication.repository.EmployeeRepository employeeRepository;

    // ========== MASTER DATA MANAGEMENT ==========

    @GetMapping("/master-data")
    public String masterDataList(@RequestParam(required = false) String type, Model model) {
        try {
            List<MasterData> masterDataList;
            if (type != null && !type.isEmpty()) {
                masterDataList = masterDataService.findByDataType(type);
            } else {
                masterDataList = masterDataService.findAll();
            }
            model.addAttribute("masterDataList", masterDataList != null ? masterDataList : List.of());
            model.addAttribute("selectedType", type);
            return "admin/master-data/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("masterDataList", List.of());
            return "admin/master-data/list";
        }
    }

    @GetMapping("/master-data/form")
    public String showMasterDataForm(@RequestParam(required = false) Long id, Model model) {
        MasterData masterData = id != null ? masterDataService.findById(id) : new MasterData();
        model.addAttribute("masterData", masterData);
        return "admin/master-data/form";
    }

    @PostMapping("/master-data/save")
    public String saveMasterData(@Valid @ModelAttribute MasterData masterData,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/master-data/form";
        }

        try {
            if (masterData.getId() != null) {
                masterDataService.update(masterData.getId(), masterData);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật dữ liệu thành công!");
            } else {
                masterDataService.save(masterData);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm dữ liệu thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/master-data/form" + (masterData.getId() != null ? "?id=" + masterData.getId() : "");
        }

        return "redirect:/admin/master-data";
    }

    @PostMapping("/master-data/delete/{id}")
    public String deleteMasterData(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            masterDataService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa dữ liệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/master-data";
    }

    @PostMapping("/master-data/deactivate/{id}")
    public String deactivateMasterData(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            masterDataService.deactivate(id);
            redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa dữ liệu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/master-data";
    }

    // ========== SYSTEM CONFIG MANAGEMENT ==========

    @GetMapping("/system-config")
    public String systemConfigList(@RequestParam(required = false) String type, Model model) {
        try {
            List<SystemConfig> configList;
            if (type != null && !type.isEmpty()) {
                configList = systemConfigService.findByType(type);
            } else {
                configList = systemConfigService.findAll();
            }
            model.addAttribute("configList", configList != null ? configList : List.of());
            model.addAttribute("selectedType", type);
            return "admin/system-config/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("configList", List.of());
            return "admin/system-config/list";
        }
    }

    @GetMapping("/system-config/form")
    public String showSystemConfigForm(@RequestParam(required = false) Long id, Model model) {
        SystemConfig systemConfig = id != null ? systemConfigService.findById(id) : new SystemConfig();
        model.addAttribute("systemConfig", systemConfig);
        return "admin/system-config/form";
    }

    @PostMapping("/system-config/save")
    public String saveSystemConfig(@Valid @ModelAttribute SystemConfig systemConfig,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/system-config/form";
        }

        try {
            if (systemConfig.getId() != null) {
                systemConfigService.update(systemConfig.getId(), systemConfig);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật cấu hình thành công!");
            } else {
                systemConfigService.save(systemConfig);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm cấu hình thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/system-config/form" + (systemConfig.getId() != null ? "?id=" + systemConfig.getId() : "");
        }

        return "redirect:/admin/system-config";
    }

    @PostMapping("/system-config/delete/{id}")
    public String deleteSystemConfig(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            systemConfigService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa cấu hình thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/system-config";
    }

    // ========== AUDIT LOGS ==========

    @GetMapping("/audit-logs")
    public String auditLogs(@RequestParam(required = false) String action,
                           @RequestParam(required = false) String entityType,
                           @RequestParam(required = false) String startDate,
                           @RequestParam(required = false) String endDate,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           Model model) {
        try {
            Pageable pageRequest = PageRequest.of(page, size);
            Page<com.example.hrmapplication.entity.AuditLog> auditLogs;

            if (action != null && !action.isEmpty()) {
                auditLogs = auditLogService.findByAction(action, pageRequest);
            } else if (entityType != null && !entityType.isEmpty()) {
                auditLogs = auditLogService.findByEntityType(entityType, pageRequest);
            } else if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
                java.time.LocalDateTime start = java.time.LocalDateTime.parse(startDate + "T00:00:00");
                java.time.LocalDateTime end = java.time.LocalDateTime.parse(endDate + "T23:59:59");
                auditLogs = auditLogService.findByDateRange(start, end, pageRequest);
            } else {
                auditLogs = auditLogService.findAll(pageRequest);
            }

            model.addAttribute("auditLogs", auditLogs);
            model.addAttribute("action", action);
            model.addAttribute("entityType", entityType);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", auditLogs.getTotalPages());
            return "admin/audit-logs/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin/audit-logs/list";
        }
    }

    // ========== BACKUP & RESTORE ==========

    @GetMapping("/backup")
    public String backupPage(Model model) {
        try {
            model.addAttribute("message", "Chức năng backup đang được phát triển. Sẽ hỗ trợ export toàn bộ dữ liệu ra file SQL.");
            return "admin/backup";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin/backup";
        }
    }

    @PostMapping("/backup/create")
    public org.springframework.http.ResponseEntity<String> createBackup() {
        try {
            // TODO: Implement backup logic
            return org.springframework.http.ResponseEntity.ok("Backup created successfully");
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/backup/list")
    public String listBackups(Model model) {
        try {
            // TODO: List backup files
            model.addAttribute("backups", List.of());
            return "admin/backup-list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin/backup-list";
        }
    }

    // ========== QUẢN LÝ NGƯỜI DÙNG KEYCLOAK ==========

    @GetMapping("/users")
    public String keycloakUsers(Model model) {
        try {
            List<org.keycloak.representations.idm.UserRepresentation> users = keycloakUserService.getAllUsers();
            List<org.keycloak.representations.idm.RoleRepresentation> allRoles = keycloakUserService.getAllRealmRoles();
            
            model.addAttribute("users", users != null ? users : List.of());
            model.addAttribute("allRoles", allRoles != null ? allRoles : List.of());
            return "admin/users/list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            model.addAttribute("users", List.of());
            model.addAttribute("allRoles", List.of());
            return "admin/users/list";
        }
    }

    @GetMapping("/users/form")
    public String showUserForm(Model model) {
        try {
            List<org.keycloak.representations.idm.RoleRepresentation> allRoles = keycloakUserService.getAllRealmRoles();
            model.addAttribute("allRoles", allRoles != null ? allRoles : List.of());
            return "admin/users/form";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin/users/form";
        }
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String username,
                            @RequestParam String email,
                            @RequestParam(required = false) String firstName,
                            @RequestParam(required = false) String lastName,
                            @RequestParam String password,
                            @RequestParam(required = false) List<String> roles,
                            RedirectAttributes redirectAttributes) {
        try {
            String userId = keycloakUserService.createUser(username, email, firstName, lastName, password, roles);
            if (userId != null) {
                redirectAttributes.addFlashAttribute("successMessage", "Tạo tài khoản thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể tạo tài khoản. Có thể username đã tồn tại.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/enable")
    public String enableUser(@PathVariable String userId, RedirectAttributes redirectAttributes) {
        try {
            boolean success = keycloakUserService.enableUser(userId);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Kích hoạt tài khoản thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể kích hoạt tài khoản.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/disable")
    public String disableUser(@PathVariable String userId, RedirectAttributes redirectAttributes) {
        try {
            boolean success = keycloakUserService.disableUser(userId);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Vô hiệu hóa tài khoản thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể vô hiệu hóa tài khoản.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{userId}/roles")
    public String showUserRoles(@PathVariable String userId, Model model) {
        try {
            org.keycloak.representations.idm.UserRepresentation user = keycloakUserService.getUserById(userId);
            List<org.keycloak.representations.idm.RoleRepresentation> userRoles = keycloakUserService.getUserRoles(userId);
            List<org.keycloak.representations.idm.RoleRepresentation> allRoles = keycloakUserService.getAllRealmRoles();
            
            model.addAttribute("user", user);
            model.addAttribute("userRoles", userRoles != null ? userRoles : List.of());
            model.addAttribute("allRoles", allRoles != null ? allRoles : List.of());
            return "admin/users/roles";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/{userId}/roles/assign")
    public String assignRoles(@PathVariable String userId,
                             @RequestParam List<String> roles,
                             RedirectAttributes redirectAttributes) {
        try {
            boolean success = keycloakUserService.assignRoles(userId, roles);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Gán roles thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể gán roles.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users/" + userId + "/roles";
    }

    @PostMapping("/users/{userId}/roles/remove")
    public String removeRoles(@PathVariable String userId,
                             @RequestParam List<String> roles,
                             RedirectAttributes redirectAttributes) {
        try {
            boolean success = keycloakUserService.removeRoles(userId, roles);
            if (success) {
                redirectAttributes.addFlashAttribute("successMessage", "Xóa roles thành công!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa roles.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users/" + userId + "/roles";
    }

    // ========== ĐỒNG BỘ KEYCLOAK ==========

    @GetMapping("/keycloak/sync")
    public String showSyncPage(Model model) {
        try {
            long unlinkedEmployees = keycloakSyncService.countUnlinkedEmployees();
            long unlinkedKeycloakUsers = keycloakSyncService.countUnlinkedKeycloakUsers();
            
            // Lấy danh sách employees chưa liên kết
            List<com.example.hrmapplication.entity.Employee> unlinkedEmployeeList = 
                    employeeRepository.findAll().stream()
                    .filter(e -> e.getKeycloakUserId() == null || e.getKeycloakUserId().isEmpty())
                    .toList();
            
            // Lấy danh sách Keycloak users
            List<org.keycloak.representations.idm.UserRepresentation> keycloakUsers = 
                    keycloakUserService.getAllUsers();
            
            model.addAttribute("unlinkedEmployees", unlinkedEmployees);
            model.addAttribute("unlinkedKeycloakUsers", unlinkedKeycloakUsers);
            model.addAttribute("unlinkedEmployeeList", unlinkedEmployeeList);
            model.addAttribute("keycloakUsers", keycloakUsers);
            return "admin/keycloak/sync";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "admin/keycloak/sync";
        }
    }

    @PostMapping("/keycloak/sync")
    public String syncKeycloakUsers(RedirectAttributes redirectAttributes) {
        try {
            int syncedCount = keycloakSyncService.syncAllUsers();
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Đã đồng bộ " + syncedCount + " users từ Keycloak với database!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/keycloak/sync";
    }
}

