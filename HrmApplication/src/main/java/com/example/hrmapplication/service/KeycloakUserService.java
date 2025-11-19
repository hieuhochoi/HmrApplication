package com.example.hrmapplication.service;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakUserService {

    private static final Logger log = LoggerFactory.getLogger(KeycloakUserService.class);

    @Value("${keycloak.server-url:http://localhost:8080}")
    private String serverUrl;

    @Value("${keycloak.realm:hrm-realm}")
    private String realm;

    @Value("${keycloak.admin-client-id:admin-cli}")
    private String adminClientId;

    @Value("${keycloak.admin-username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin-password:admin}")
    private String adminPassword;

    private Keycloak getKeycloakAdminClient() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master") // Admin realm
                .username(adminUsername)
                .password(adminPassword)
                .clientId(adminClientId)
                .build();
    }

    private RealmResource getRealm() {
        return getKeycloakAdminClient().realm(realm);
    }

    public List<UserRepresentation> getAllUsers() {
        try {
            UsersResource usersResource = getRealm().users();
            return usersResource.list();
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách users từ Keycloak: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public UserRepresentation getUserById(String userId) {
        try {
            UsersResource usersResource = getRealm().users();
            return usersResource.get(userId).toRepresentation();
        } catch (Exception e) {
            log.error("Lỗi khi lấy user từ Keycloak: {}", e.getMessage(), e);
            return null;
        }
    }

    public UserRepresentation getUserByUsername(String username) {
        try {
            UsersResource usersResource = getRealm().users();
            List<UserRepresentation> users = usersResource.search(username, true);
            return users.isEmpty() ? null : users.get(0);
        } catch (Exception e) {
            log.error("Lỗi khi tìm user từ Keycloak: {}", e.getMessage(), e);
            return null;
        }
    }

    public String createUser(String username, String email, String firstName, String lastName, String password, List<String> roles) {
        try {
            UsersResource usersResource = getRealm().users();
            
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            user.setEmailVerified(false);

            // Tạo user
            Response response = usersResource.create(user);
            
            if (response.getStatus() == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                
                // Set password
                if (password != null && !password.isEmpty()) {
                    CredentialRepresentation credential = new CredentialRepresentation();
                    credential.setType(CredentialRepresentation.PASSWORD);
                    credential.setValue(password);
                    credential.setTemporary(false);
                    
                    UserResource userResource = usersResource.get(userId);
                    userResource.resetPassword(credential);
                }
                
                // Assign roles
                if (roles != null && !roles.isEmpty()) {
                    assignRoles(userId, roles);
                }
                
                return userId;
            } else {
                log.error("Lỗi khi tạo user trong Keycloak. Status: {}", response.getStatus());
                return null;
            }
        } catch (Exception e) {
            log.error("Lỗi khi tạo user trong Keycloak: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean updateUser(String userId, String email, String firstName, String lastName) {
        try {
            UsersResource usersResource = getRealm().users();
            UserResource userResource = usersResource.get(userId);
            UserRepresentation user = userResource.toRepresentation();
            
            if (email != null) user.setEmail(email);
            if (firstName != null) user.setFirstName(firstName);
            if (lastName != null) user.setLastName(lastName);
            
            userResource.update(user);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật user trong Keycloak: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean enableUser(String userId) {
        try {
            UsersResource usersResource = getRealm().users();
            UserResource userResource = usersResource.get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(true);
            userResource.update(user);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi kích hoạt user trong Keycloak: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean disableUser(String userId) {
        try {
            UsersResource usersResource = getRealm().users();
            UserResource userResource = usersResource.get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(false);
            userResource.update(user);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi vô hiệu hóa user trong Keycloak: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean assignRoles(String userId, List<String> roleNames) {
        try {
            UsersResource usersResource = getRealm().users();
            UserResource userResource = usersResource.get(userId);
            
            List<RoleRepresentation> rolesToAssign = roleNames.stream()
                    .map(roleName -> getRealm().roles().get(roleName).toRepresentation())
                    .collect(Collectors.toList());
            
            userResource.roles().realmLevel().add(rolesToAssign);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi gán roles cho user trong Keycloak: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean removeRoles(String userId, List<String> roleNames) {
        try {
            UsersResource usersResource = getRealm().users();
            UserResource userResource = usersResource.get(userId);
            
            List<RoleRepresentation> rolesToRemove = roleNames.stream()
                    .map(roleName -> getRealm().roles().get(roleName).toRepresentation())
                    .collect(Collectors.toList());
            
            userResource.roles().realmLevel().remove(rolesToRemove);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xóa roles của user trong Keycloak: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<RoleRepresentation> getUserRoles(String userId) {
        try {
            UsersResource usersResource = getRealm().users();
            UserResource userResource = usersResource.get(userId);
            return userResource.roles().realmLevel().listAll();
        } catch (Exception e) {
            log.error("Lỗi khi lấy roles của user từ Keycloak: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public List<RoleRepresentation> getAllRealmRoles() {
        try {
            return getRealm().roles().list();
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách roles từ Keycloak: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public boolean deleteUser(String userId) {
        try {
            UsersResource usersResource = getRealm().users();
            usersResource.get(userId).remove();
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi xóa user trong Keycloak: {}", e.getMessage(), e);
            return false;
        }
    }
}

