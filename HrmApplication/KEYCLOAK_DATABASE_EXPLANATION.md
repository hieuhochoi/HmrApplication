# Giải thích về Keycloak và Database

## Vấn đề bạn đang gặp

Bạn thắc mắc tại sao Keycloak không lấy được dữ liệu từ database local của bạn.

## Giải thích

### 1. Keycloak có database riêng

**Keycloak KHÔNG sử dụng database của ứng dụng Spring Boot.**

- Keycloak có database riêng của nó để lưu:
  - Users (người dùng)
  - Realms (các realm)
  - Clients (các ứng dụng OAuth2)
  - Roles (vai trò)
  - Sessions (phiên đăng nhập)
  
- Database của Keycloak thường là:
  - **H2 embedded** (mặc định, chỉ dùng cho development)
  - **PostgreSQL** (production)
  - **MySQL** (production)

### 2. Ứng dụng Spring Boot có database riêng

- Database của bạn: `hrm_db` (MySQL trên port 3308)
- Lưu trữ:
  - Employees (nhân viên)
  - Salaries (lương)
  - Contracts (hợp đồng)
  - Attendances (chấm công)
  - ... (tất cả dữ liệu nghiệp vụ)

### 3. Cách chúng hoạt động cùng nhau

```
┌─────────────────┐         ┌──────────────────┐         ┌─────────────────┐
│   Keycloak      │         │  Spring Boot     │         │   MySQL Local   │
│   (Port 8080)   │◄───────►│  (Port 8081)     │◄───────►│   (Port 3308)   │
│                 │         │                  │         │                 │
│ - Users         │         │ - Employees      │         │ - employees     │
│ - Roles         │         │ - Salaries       │         │ - salaries      │
│ - Sessions      │         │ - Contracts      │         │ - contracts     │
│                 │         │ - ...            │         │ - ...           │
└─────────────────┘         └──────────────────┘         └─────────────────┘
     Database               Application Database         hrm_db Database
  (Keycloak DB)            (Business Logic)
```

### 4. Liên kết giữa Keycloak và Database

Hệ thống liên kết Keycloak user với Employee trong database qua:

1. **Trường `keycloak_user_id`** trong bảng `employees`
2. **Tự động liên kết theo email** (đã được implement trong `DashboardController`)

```java
// Tự động liên kết khi đăng nhập
if (currentEmployee == null && principal.getEmail() != null) {
    employeeRepository.findByEmail(principal.getEmail())
        .ifPresent(emp -> {
            emp.setKeycloakUserId(userId);
            employeeRepository.save(emp);
        });
}
```

### 5. Cách kiểm tra kết nối

#### Kiểm tra Keycloak:
1. Truy cập: http://localhost:8080
2. Đăng nhập với admin/admin
3. Vào Realm "hrm-realm" → Users → Xem danh sách users

#### Kiểm tra Database local:
1. Kết nối MySQL: `localhost:3308`
2. Database: `hrm_db`
3. Kiểm tra bảng `employees`:
   ```sql
   SELECT id, full_name, email, keycloak_user_id FROM employees;
   ```

### 6. Cách đồng bộ dữ liệu

#### Tạo user mới:
1. **Tạo trong Keycloak** (qua Admin UI hoặc API)
2. **Tạo Employee trong database** với `keycloak_user_id` tương ứng
3. Hoặc dùng tính năng tự động liên kết theo email

#### Đồng bộ thủ công:
```sql
-- Cập nhật keycloak_user_id cho employee
UPDATE employees 
SET keycloak_user_id = 'keycloak-user-id-here' 
WHERE email = 'user@example.com';
```

### 7. Lưu ý quan trọng

- **Keycloak KHÔNG đọc dữ liệu từ database local của bạn**
- **Ứng dụng Spring Boot mới đọc từ database local**
- **Cần liên kết thủ công hoặc tự động** giữa Keycloak user và Employee

### 8. Nếu muốn Keycloak dùng database của bạn

Bạn cần cấu hình Keycloak để kết nối với MySQL của bạn (không khuyến nghị vì phức tạp và không cần thiết).

Thay vào đó, giữ 2 database riêng biệt và liên kết qua `keycloak_user_id` như hiện tại.

