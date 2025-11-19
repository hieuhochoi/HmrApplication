# Hệ thống phân quyền HRM

## Tổng quan

Hệ thống HRM sử dụng **4 bậc phân quyền** theo mô hình phân cấp:

```
ADMIN (Quyền cao nhất)
  └─ HR (Phòng nhân sự)
      └─ MANAGER (Trưởng phòng)
          └─ EMPLOYEE (Nhân viên)
```

## 1. Các bậc phân quyền

### 🔴 BẬC 1: ADMIN (Quản trị viên)
- **Quyền cao nhất** trong hệ thống
- **Chỉ ADMIN** mới có quyền truy cập
- **Không kế thừa** quyền từ bất kỳ role nào

**Chức năng:**
- ✅ Quản lý Master Data (Phòng ban, Vị trí, Bậc lương, Ca làm việc, Loại hợp đồng, Bảo hiểm, Quy định phép)
- ✅ Quản lý System Config (Cấu hình hệ thống)
- ✅ Quản lý Audit Logs (Nhật ký thao tác)
- ✅ Quản lý Backup & Restore
- ✅ Quản lý Keycloak Users (Tạo, sửa, xóa, bật/tắt tài khoản)
- ✅ Phân quyền cho users (Gán/xóa roles)
- ✅ Đồng bộ Keycloak với Database

**Routes:**
- `/admin/**` - Chỉ ADMIN
- `/api/admin/**` - Chỉ ADMIN

**Annotation:**
```java
@PreAuthorize("hasRole('ADMIN')")
```

---

### 🟡 BẬC 2: HR (Phòng nhân sự)
- **Kế thừa quyền từ ADMIN** (ADMIN có thể làm tất cả chức năng HR)
- **Có quyền** truy cập các chức năng HR

**Chức năng:**
- ✅ Quản lý hồ sơ nhân viên (Thêm, sửa, xem, lưu trữ)
- ✅ Quản lý hợp đồng (Tạo, gia hạn, chuyển loại, thông báo hết hạn)
- ✅ Quản lý bảo hiểm (Đăng ký, báo tăng/giảm)
- ✅ Quản lý chấm công (Duyệt công, điều chỉnh công sai)
- ✅ Tính lương (Khóa bảng công, tính lương tự động, export Excel/PDF)
- ✅ Gửi phiếu lương qua email
- ✅ Duyệt yêu cầu nghỉ phép
- ✅ Duyệt yêu cầu tăng ca
- ✅ Duyệt hồ sơ thay đổi thông tin nhân viên
- ✅ Chuyển phòng ban/chức vụ nhân viên

**Routes:**
- `/hr/**` - ADMIN và HR
- `/api/hr/**` - ADMIN và HR

**Annotation:**
```java
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")
```

---

### 🟢 BẬC 3: MANAGER (Trưởng phòng)
- **Kế thừa quyền từ ADMIN và HR** (ADMIN và HR có thể làm tất cả chức năng Manager)
- **Chỉ quản lý nhân viên trong phòng ban của mình**

**Chức năng:**
- ✅ Xem danh sách nhân viên trong phòng ban (chỉ xem, không sửa)
- ✅ Duyệt yêu cầu nghỉ phép của nhân viên trong phòng ban
- ✅ Duyệt yêu cầu tăng ca của nhân viên trong phòng ban
- ✅ Duyệt yêu cầu điều chỉnh công của nhân viên trong phòng ban
- ✅ Xem báo cáo phòng ban (Thống kê nhân viên, đi muộn, vắng mặt, biểu đồ)

**Routes:**
- `/manager/**` - ADMIN, HR, và MANAGER
- `/api/manager/**` - ADMIN, HR, và MANAGER

**Annotation:**
```java
@PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
```

**Lưu ý:**
- Manager chỉ thấy được nhân viên có `manager_id` trỏ đến mình
- Manager không thể sửa hồ sơ nhân viên (chỉ HR làm)

---

### 🔵 BẬC 4: EMPLOYEE (Nhân viên)
- **Bậc thấp nhất** - Tất cả users đã đăng nhập đều có quyền này
- **Chỉ quản lý thông tin cá nhân**

**Chức năng:**
- ✅ Xem hồ sơ cá nhân
- ✅ Cập nhật một số thông tin (Số điện thoại, địa chỉ)
- ✅ Xem bảng công cá nhân
- ✅ Gửi yêu cầu điều chỉnh công nếu sai
- ✅ Gửi yêu cầu nghỉ phép
- ✅ Theo dõi số ngày phép còn lại
- ✅ Theo dõi trạng thái phê duyệt nghỉ phép
- ✅ Gửi đăng ký tăng ca (OT)
- ✅ Xem phiếu lương cá nhân
- ✅ Tải phiếu lương PDF

**Routes:**
- `/employee/**` - Tất cả users đã đăng nhập
- `/my/**` - Tất cả users đã đăng nhập
- `/api/employee/**` - Tất cả users đã đăng nhập

**Annotation:**
```java
@PreAuthorize("isAuthenticated()")
```

---

## 2. Cấu hình phân quyền

### SecurityConfig.java

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    // Routes phân quyền
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/hr/**").hasAnyRole("ADMIN", "HR")
    .requestMatchers("/manager/**").hasAnyRole("ADMIN", "HR", "MANAGER")
    .requestMatchers("/employee/**", "/my/**").authenticated()
}
```

### Lấy roles từ Keycloak

Roles được lấy từ Keycloak token qua `realm_access.roles`:

```java
// SecurityConfig.java - authoritiesMapper()
Map<String, Object> realmAccess = (Map<String, Object>) userAttributes.get("realm_access");
Collection<String> roles = (Collection<String>) realmAccess.get("roles");
roles.forEach(role -> {
    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
});
```

### SecurityUtil.java

```java
// Lấy danh sách roles của user hiện tại
List<String> roles = SecurityUtil.getCurrentUserRoles();

// Kiểm tra có role cụ thể không (ADMIN luôn có tất cả quyền)
boolean hasRole = SecurityUtil.hasRole("HR");

// Kiểm tra có bất kỳ role nào trong danh sách
boolean hasAnyRole = SecurityUtil.hasAnyRole("HR", "MANAGER");
```

**Lưu ý:** ADMIN luôn có tất cả quyền (được xử lý trong `hasRole()` và `hasAnyRole()`)

---

## 3. Phân quyền trong Controllers

### AdminController
```java
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")  // Chỉ ADMIN
public class AdminController {
    // Tất cả methods trong controller này chỉ ADMIN mới truy cập được
}
```

### HRController
```java
@Controller
@RequestMapping("/hr")
@PreAuthorize("hasAnyRole('ADMIN', 'HR')")  // ADMIN hoặc HR
public class HRController {
    // ADMIN và HR đều có thể truy cập
}
```

### ManagerController
```java
@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")  // ADMIN, HR, hoặc MANAGER
public class ManagerController {
    // ADMIN, HR, và MANAGER đều có thể truy cập
    // Nhưng logic bên trong chỉ cho Manager thấy nhân viên của mình
}
```

### EmployeeController
```java
@Controller
@RequestMapping("/employee")
@PreAuthorize("isAuthenticated()")  // Tất cả users đã đăng nhập
public class EmployeeController {
    // Tất cả users đã đăng nhập đều có thể truy cập
    // Logic bên trong chỉ cho user thấy thông tin của chính mình
}
```

---

## 4. Phân quyền trong Templates (Thymeleaf)

### Kiểm tra role trong template

```html
<!-- Chỉ hiển thị cho ADMIN -->
<div th:if="${#lists.contains(roles, 'ADMIN')}">
    <a href="/admin/users">Quản lý Users</a>
</div>

<!-- Hiển thị cho ADMIN và HR -->
<div th:if="${#lists.contains(roles, 'ADMIN') or #lists.contains(roles, 'HR')}">
    <a href="/hr/employees">Quản lý Nhân viên</a>
</div>

<!-- Hiển thị cho ADMIN, HR, và MANAGER -->
<div th:if="${#lists.contains(roles, 'ADMIN') or #lists.contains(roles, 'HR') or #lists.contains(roles, 'MANAGER')}">
    <a href="/manager/reports">Báo cáo phòng ban</a>
</div>
```

### Dashboard routing

```java
// DashboardController.java
if (roles.contains("ADMIN")) {
    return redirectToAdminDashboard(model);
} else if (roles.contains("HR")) {
    return redirectToHRDashboard(model);
} else if (roles.contains("MANAGER")) {
    return redirectToManagerDashboard(model, currentEmployee);
} else {
    return redirectToEmployeeDashboard(model, currentEmployee);
}
```

---

## 5. Quản lý roles trong Keycloak

### Tạo và gán roles

1. **Truy cập Keycloak Admin UI:**
   - URL: http://localhost:8080
   - Đăng nhập: admin/admin
   - Vào Realm: `hrm-realm`

2. **Tạo Realm Roles:**
   - Vào **Realm roles** → **Create role**
   - Tạo các roles: `ADMIN`, `HR`, `MANAGER`, `EMPLOYEE`

3. **Gán roles cho user:**
   - Vào **Users** → Chọn user → **Role mapping**
   - Chọn **Assign role** → Chọn realm role cần gán

### Quản lý roles qua AdminController

- **Xem danh sách users:** `/admin/users`
- **Gán roles:** `/admin/users/{userId}/roles`
- **Xóa roles:** `/admin/users/{userId}/roles` (POST remove)

---

## 6. Bảng tổng hợp quyền hạn

| Chức năng | ADMIN | HR | MANAGER | EMPLOYEE |
|-----------|:-----:|:--:|:-------:|:--------:|
| **Quản lý Master Data** | ✅ | ❌ | ❌ | ❌ |
| **Quản lý System Config** | ✅ | ❌ | ❌ | ❌ |
| **Quản lý Audit Logs** | ✅ | ❌ | ❌ | ❌ |
| **Quản lý Keycloak Users** | ✅ | ❌ | ❌ | ❌ |
| **Quản lý hồ sơ nhân viên** | ✅ | ✅ | ❌ | ❌ |
| **Quản lý hợp đồng** | ✅ | ✅ | ❌ | ❌ |
| **Quản lý bảo hiểm** | ✅ | ✅ | ❌ | ❌ |
| **Tính lương** | ✅ | ✅ | ❌ | ❌ |
| **Duyệt nghỉ phép (tất cả)** | ✅ | ✅ | ❌ | ❌ |
| **Duyệt tăng ca (tất cả)** | ✅ | ✅ | ❌ | ❌ |
| **Xem nhân viên phòng ban** | ✅ | ✅ | ✅ | ❌ |
| **Duyệt nghỉ phép (phòng ban)** | ✅ | ✅ | ✅ | ❌ |
| **Duyệt tăng ca (phòng ban)** | ✅ | ✅ | ✅ | ❌ |
| **Báo cáo phòng ban** | ✅ | ✅ | ✅ | ❌ |
| **Xem hồ sơ cá nhân** | ✅ | ✅ | ✅ | ✅ |
| **Gửi yêu cầu nghỉ phép** | ✅ | ✅ | ✅ | ✅ |
| **Gửi yêu cầu tăng ca** | ✅ | ✅ | ✅ | ✅ |
| **Xem phiếu lương** | ✅ | ✅ | ✅ | ✅ |

**Chú thích:**
- ✅ = Có quyền
- ❌ = Không có quyền

---

## 7. Lưu ý quan trọng

### 1. ADMIN có tất cả quyền
- ADMIN tự động có quyền của HR, MANAGER, và EMPLOYEE
- Được xử lý trong `SecurityUtil.hasRole()` và `SecurityUtil.hasAnyRole()`

### 2. Manager chỉ quản lý nhân viên của mình
- Manager chỉ thấy nhân viên có `manager_id` trỏ đến mình
- Được kiểm tra qua `employeeRepository.findByManagerId(manager.getId())`

### 3. Employee chỉ thấy thông tin của mình
- Employee chỉ thấy hồ sơ, lương, chấm công của chính mình
- Được kiểm tra qua `employeeRepository.findByKeycloakUserId(userId)`

### 4. Roles được lấy từ Keycloak
- Roles không lưu trong database local
- Roles được lấy từ Keycloak token mỗi lần request
- Cần đảm bảo Keycloak đang chạy và cấu hình đúng

### 5. Phân quyền 2 lớp
- **Lớp 1:** Spring Security (URL level) - Chặn ở controller
- **Lớp 2:** Business Logic (Data level) - Kiểm tra trong service/repository

---

## 8. Ví dụ phân quyền thực tế

### Ví dụ 1: Manager duyệt nghỉ phép

```java
@GetMapping("/manager/leave-requests")
public String pendingLeaveRequests(Model model) {
    // 1. Kiểm tra user có role MANAGER (hoặc ADMIN/HR)
    //    → Đã được xử lý bởi @PreAuthorize
    
    // 2. Lấy manager từ Keycloak user ID
    Employee manager = employeeRepository.findByKeycloakUserId(keycloakUserId);
    
    // 3. Lấy danh sách nhân viên của manager
    List<Employee> subordinates = employeeRepository.findByManagerId(manager.getId());
    
    // 4. Chỉ lấy yêu cầu nghỉ phép của nhân viên trong phòng ban
    List<LeaveRequest> pendingRequests = allPendingRequests.stream()
        .filter(request -> subordinateIds.contains(request.getEmployee().getId()))
        .collect(Collectors.toList());
    
    // Manager chỉ thấy yêu cầu của nhân viên mình quản lý
}
```

### Ví dụ 2: Employee xem lương

```java
@GetMapping("/employee/salaries")
public String mySalaries(Model model) {
    // 1. Kiểm tra user đã đăng nhập
    //    → Đã được xử lý bởi @PreAuthorize("isAuthenticated()")
    
    // 2. Lấy employee từ Keycloak user ID
    Employee employee = employeeRepository.findByKeycloakUserId(keycloakUserId);
    
    // 3. Chỉ lấy lương của chính employee đó
    List<Salary> salaries = salaryService.findByEmployeeId(employee.getId());
    
    // Employee chỉ thấy lương của chính mình
}
```

---

## 9. Troubleshooting

### Vấn đề: User không thể truy cập chức năng

**Kiểm tra:**
1. User có role tương ứng trong Keycloak không?
2. Role có được gán đúng realm role không?
3. Keycloak đang chạy và cấu hình đúng không?
4. Token có chứa roles trong `realm_access.roles` không?

**Debug:**
```java
// Thêm vào controller để debug
List<String> roles = SecurityUtil.getCurrentUserRoles();
log.info("User roles: {}", roles);
```

### Vấn đề: Manager không thấy nhân viên

**Kiểm tra:**
1. Employee có `manager_id` trỏ đến Manager không?
2. Manager có `current_department_id` không?
3. Employee có cùng `current_department_id` với Manager không?

---

## 10. Tổng kết

Hệ thống phân quyền HRM sử dụng **4 bậc phân quyền** với mô hình kế thừa:

- **ADMIN** → Quyền cao nhất, có tất cả quyền
- **HR** → Quản lý nhân sự, kế thừa từ ADMIN
- **MANAGER** → Quản lý phòng ban, kế thừa từ ADMIN và HR
- **EMPLOYEE** → Quản lý cá nhân, kế thừa từ tất cả roles trên

Phân quyền được thực hiện ở **2 lớp:**
1. **URL level** (Spring Security) - Chặn ở controller
2. **Data level** (Business Logic) - Kiểm tra trong service/repository

Roles được quản lý trong **Keycloak** và được lấy từ token mỗi lần request.

