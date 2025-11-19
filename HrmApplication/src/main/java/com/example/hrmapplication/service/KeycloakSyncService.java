package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.repository.EmployeeRepository;
import com.example.hrmapplication.service.KeycloakUserService;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service để đồng bộ dữ liệu giữa Keycloak và Database local
 */
@Service
public class KeycloakSyncService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakSyncService.class);

    @Autowired
    private KeycloakUserService keycloakUserService;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Kiểm tra và đồng bộ tất cả users từ Keycloak với database
     * Liên kết theo email nếu chưa có keycloak_user_id
     */
    public int syncAllUsers() {
        try {
            List<UserRepresentation> keycloakUsers = keycloakUserService.getAllUsers();
            int syncedCount = 0;

            for (UserRepresentation keycloakUser : keycloakUsers) {
                try {
                    String keycloakUserId = keycloakUser.getId();
                    String email = keycloakUser.getEmail();

                    // Tìm employee theo keycloak_user_id
                    Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId)
                            .orElse(null);

                    // Nếu chưa có, thử tìm theo email
                    if (employee == null && email != null && !email.isEmpty()) {
                        employee = employeeRepository.findByEmail(email).orElse(null);
                        if (employee != null && (employee.getKeycloakUserId() == null || employee.getKeycloakUserId().isEmpty())) {
                            // Liên kết với Keycloak user
                            employee.setKeycloakUserId(keycloakUserId);
                            employeeRepository.save(employee);
                            syncedCount++;
                            log.info("Đã liên kết employee {} với Keycloak user {}", employee.getFullName(), keycloakUserId);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Lỗi khi đồng bộ user {}: {}", keycloakUser.getUsername(), e.getMessage());
                }
            }

            log.info("Đã đồng bộ {} users từ Keycloak", syncedCount);
            return syncedCount;
        } catch (Exception e) {
            log.error("Lỗi khi đồng bộ users từ Keycloak: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Kiểm tra trạng thái đồng bộ
     * @return số lượng employees chưa được liên kết với Keycloak
     */
    public long countUnlinkedEmployees() {
        return employeeRepository.findAll().stream()
                .filter(e -> e.getKeycloakUserId() == null || e.getKeycloakUserId().isEmpty())
                .count();
    }

    /**
     * Kiểm tra số lượng Keycloak users chưa có Employee tương ứng
     */
    public long countUnlinkedKeycloakUsers() {
        try {
            List<UserRepresentation> keycloakUsers = keycloakUserService.getAllUsers();
            return keycloakUsers.stream()
                    .filter(ku -> {
                        String keycloakUserId = ku.getId();
                        String email = ku.getEmail();
                        
                        // Kiểm tra xem có employee nào liên kết không
                        boolean hasEmployee = employeeRepository.findByKeycloakUserId(keycloakUserId).isPresent();
                        if (!hasEmployee && email != null && !email.isEmpty()) {
                            hasEmployee = employeeRepository.findByEmail(email).isPresent();
                        }
                        return !hasEmployee;
                    })
                    .count();
        } catch (Exception e) {
            log.error("Lỗi khi đếm unlinked Keycloak users: {}", e.getMessage());
            return 0;
        }
    }
}

