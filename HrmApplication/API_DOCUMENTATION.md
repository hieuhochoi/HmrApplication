# 📚 TÀI LIỆU API - HRM SYSTEM

## 🔍 TỔNG QUAN

Dự án HRM System hiện tại sử dụng **Spring MVC với Thymeleaf**, tất cả các endpoint đều trả về **HTML views** (không phải REST API JSON).

- **Base URL**: `http://localhost:8081`
- **Authentication**: OAuth2 với Keycloak
- **Response Format**: HTML (Thymeleaf templates)

---

## ⚠️ LƯU Ý QUAN TRỌNG

### Test API qua Postman:
- ✅ **Có thể test** nhưng sẽ nhận về **HTML response** (không phải JSON)
- Cần đăng nhập qua Keycloak trước để có session cookie
- Cần gửi kèm **session cookie** trong request

### Test API qua Swagger:
- ❌ **Chưa có Swagger** trong dự án hiện tại
- Cần thêm dependency và cấu hình Swagger/OpenAPI
- Xem phần "Thêm Swagger" bên dưới

---

## 📋 DANH SÁCH API THEO MODULE

### 🏠 **HOME & AUTHENTICATION**

| Method | Endpoint | Mô tả | Quyền truy cập |
|--------|----------|-------|----------------|
| GET | `/` | Trang chủ, redirect đến dashboard hoặc login | Public |
| GET | `/dashboard` | Dashboard theo role | Authenticated |
| GET | `/logout` | Đăng xuất | Authenticated |

---

### 👤 **EMPLOYEE (Nhân viên tự quản lý)**

**Base Path**: `/employee`  
**Quyền**: `isAuthenticated()`

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/employee/profile` | Xem thông tin cá nhân | - |
| GET | `/employee/profile/edit` | Form chỉnh sửa thông tin | - |
| POST | `/employee/profile/update` | Cập nhật thông tin | Form data |
| GET | `/employee/leave-requests` | Danh sách đơn nghỉ phép | - |
| GET | `/employee/leave-requests/form` | Form tạo đơn nghỉ phép | - |
| POST | `/employee/leave-requests/save` | Lưu đơn nghỉ phép | Form data |
| GET | `/employee/overtime-requests` | Danh sách đơn tăng ca | - |
| GET | `/employee/overtime-requests/form` | Form tạo đơn tăng ca | - |
| POST | `/employee/overtime-requests/save` | Lưu đơn tăng ca | Form data |
| GET | `/employee/salaries` | Danh sách lương | - |
| GET | `/employee/salaries/{id}` | Chi tiết lương | `id` |
| GET | `/employee/salaries/{id}/download` | Tải PDF phiếu lương | `id` |
| GET | `/employee/attendances` | Danh sách chấm công | `month`, `year` |
| GET | `/employee/attendance-adjustments` | Danh sách yêu cầu điều chỉnh chấm công | - |
| GET | `/employee/attendance-adjustments/form` | Form tạo yêu cầu điều chỉnh | - |
| POST | `/employee/attendance-adjustments/save` | Lưu yêu cầu điều chỉnh | Form data |

---

### 👔 **HR (Quản lý nhân sự)**

**Base Path**: `/hr`  
**Quyền**: `hasAnyRole('ADMIN', 'HR')`

#### **Quản lý nhân viên**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/hr/employees` | Danh sách nhân viên | `search`, `status`, `departmentId`, `page`, `size` |
| GET | `/hr/employees/form` | Form thêm/sửa nhân viên | `id` (optional) |
| POST | `/hr/employees/save` | Lưu nhân viên | Form data |
| GET | `/hr/employees/{id}` | Chi tiết nhân viên | `id` |
| GET | `/hr/employees/{id}/transfer` | Form chuyển phòng ban | `id` |
| POST | `/hr/employees/{id}/transfer` | Thực hiện chuyển phòng ban | Form data |

#### **Quản lý lương**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/hr/salaries` | Danh sách lương | `month`, `year`, `status`, `employeeId` |
| GET | `/hr/salaries/{id}` | Chi tiết lương | `id` |
| GET | `/hr/salaries/form` | Form thêm/sửa lương | `id` (optional) |
| POST | `/hr/salaries/save` | Lưu lương | Form data |
| POST | `/hr/salaries/{id}/approve` | Duyệt thanh toán lương | `id` |
| POST | `/hr/salaries/{id}/cancel` | Hủy thanh toán lương | `id` |
| POST | `/hr/salaries/batch-approve` | Duyệt hàng loạt | Form data (ids[]) |
| GET | `/hr/salaries/calculate` | Form tính lương tự động | - |
| POST | `/hr/salaries/calculate` | Tính lương tự động | Form data (month, year) |
| POST | `/hr/salaries/send-emails` | Gửi email phiếu lương | Form data (month, year) |
| GET | `/hr/salaries/export/excel` | Xuất Excel danh sách lương | `month`, `year` |
| GET | `/hr/salaries/export/pdf` | Xuất PDF danh sách lương | `month`, `year` |

#### **Quản lý hợp đồng**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/hr/contracts` | Danh sách hợp đồng | - |
| GET | `/hr/contracts/expiring` | Hợp đồng sắp hết hạn | `days` (default: 30) |
| GET | `/hr/contracts/{id}/renew` | Form gia hạn hợp đồng | `id` |
| POST | `/hr/contracts/{id}/renew` | Gia hạn hợp đồng | Form data |
| GET | `/hr/contracts/{id}/convert` | Form chuyển đổi hợp đồng | `id` |
| POST | `/hr/contracts/{id}/convert` | Chuyển đổi hợp đồng | Form data |

#### **Quản lý bảo hiểm**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/hr/insurances` | Danh sách bảo hiểm | - |

#### **Duyệt yêu cầu**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/hr/approvals/leave-requests` | Danh sách đơn nghỉ phép chờ duyệt | - |
| POST | `/hr/approvals/leave-requests/approve/{id}` | Duyệt đơn nghỉ phép | `id` |
| POST | `/hr/approvals/leave-requests/reject/{id}` | Từ chối đơn nghỉ phép | `id` |
| GET | `/hr/approvals/overtime-requests` | Danh sách đơn tăng ca chờ duyệt | - |
| POST | `/hr/approvals/overtime-requests/approve/{id}` | Duyệt đơn tăng ca | `id` |
| POST | `/hr/approvals/overtime-requests/reject/{id}` | Từ chối đơn tăng ca | `id` |

#### **Chấm công**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/hr/attendances` | Danh sách chấm công | `month`, `year`, `employeeId` |

---

### 👨‍💼 **MANAGER (Trưởng phòng)**

**Base Path**: `/manager`  
**Quyền**: `hasAnyRole('ADMIN', 'HR', 'MANAGER')`

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/manager/leave-requests` | Danh sách đơn nghỉ phép phòng ban | - |
| POST | `/manager/leave-requests/approve/{id}` | Duyệt đơn nghỉ phép | `id` |
| POST | `/manager/leave-requests/reject/{id}` | Từ chối đơn nghỉ phép | `id` |
| GET | `/manager/overtime-requests` | Danh sách đơn tăng ca phòng ban | - |
| POST | `/manager/overtime-requests/approve/{id}` | Duyệt đơn tăng ca | `id` |
| POST | `/manager/overtime-requests/reject/{id}` | Từ chối đơn tăng ca | `id` |
| GET | `/manager/reports` | Báo cáo phòng ban | `type`, `month`, `year` |
| GET | `/manager/attendance-adjustments` | Danh sách yêu cầu điều chỉnh chấm công | - |
| POST | `/manager/attendance-adjustments/approve/{id}` | Duyệt điều chỉnh chấm công | `id` |
| POST | `/manager/attendance-adjustments/reject/{id}` | Từ chối điều chỉnh chấm công | `id` |

---

### 🔧 **ADMIN (Quản trị hệ thống)**

**Base Path**: `/admin`  
**Quyền**: `hasRole('ADMIN')`

#### **Quản lý dữ liệu master**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/admin/master-data` | Danh sách dữ liệu master | `type` (optional) |
| GET | `/admin/master-data/form` | Form thêm/sửa master data | `id` (optional) |
| POST | `/admin/master-data/save` | Lưu master data | Form data |
| POST | `/admin/master-data/delete/{id}` | Xóa master data | `id` |
| POST | `/admin/master-data/deactivate/{id}` | Vô hiệu hóa master data | `id` |

#### **Cấu hình hệ thống**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/admin/system-config` | Danh sách cấu hình | - |
| GET | `/admin/system-config/form` | Form thêm/sửa cấu hình | `id` (optional) |
| POST | `/admin/system-config/save` | Lưu cấu hình | Form data |
| POST | `/admin/system-config/delete/{id}` | Xóa cấu hình | `id` |

#### **Quản lý người dùng Keycloak**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/admin/users` | Danh sách người dùng | - |
| GET | `/admin/users/form` | Form tạo người dùng | - |
| POST | `/admin/users/create` | Tạo người dùng Keycloak | Form data |
| POST | `/admin/users/{userId}/enable` | Kích hoạt người dùng | `userId` |
| POST | `/admin/users/{userId}/disable` | Vô hiệu hóa người dùng | `userId` |
| GET | `/admin/users/{userId}/roles` | Danh sách role của user | `userId` |
| POST | `/admin/users/{userId}/roles/assign` | Gán role cho user | Form data |
| POST | `/admin/users/{userId}/roles/remove` | Gỡ role của user | Form data |

#### **Đồng bộ Keycloak**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/admin/keycloak/sync` | Trang đồng bộ Keycloak | - |
| POST | `/admin/keycloak/sync` | Thực hiện đồng bộ | - |
| GET | `/admin/keycloak/sync/test` | Test kết nối Keycloak | - |

#### **Audit Logs & Backup**

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/admin/audit-logs` | Danh sách audit logs | `page`, `size`, `action`, `userId` |
| GET | `/admin/backup` | Trang backup | - |
| POST | `/admin/backup/create` | Tạo backup | - |
| GET | `/admin/backup/list` | Danh sách backup | - |

---

### 📊 **CRUD CONTROLLERS (Các module khác)**

#### **Phòng ban (Departments)**

**Base Path**: `/departments`

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/departments` | Danh sách phòng ban | - |
| GET | `/departments/new` | Form thêm phòng ban | - |
| POST | `/departments/save` | Lưu phòng ban | Form data |
| GET | `/departments/edit/{id}` | Form sửa phòng ban | `id` |
| GET | `/departments/delete/{id}` | Xóa phòng ban | `id` |

#### **Chức vụ (Positions)**

**Base Path**: `/positions`

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/positions` | Danh sách chức vụ | - |
| GET | `/positions/new` | Form thêm chức vụ | - |
| POST | `/positions/save` | Lưu chức vụ | Form data |
| GET | `/positions/edit/{id}` | Form sửa chức vụ | `id` |
| GET | `/positions/delete/{id}` | Xóa chức vụ | `id` |

#### **Hợp đồng (Contracts)**

**Base Path**: `/contracts`

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/contracts/employee/{employeeId}` | Hợp đồng của nhân viên | `employeeId` |
| GET | `/contracts/new` | Form thêm hợp đồng | `employeeId` (optional) |
| POST | `/contracts/save` | Lưu hợp đồng | Form data |
| GET | `/contracts/edit/{id}` | Form sửa hợp đồng | `id` |
| GET | `/contracts/detail/{id}` | Chi tiết hợp đồng | `id` |
| GET | `/contracts/delete/{id}` | Xóa hợp đồng | `id` |

#### **Bảo hiểm (Insurances)**

**Base Path**: `/insurances`

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/insurances/employee/{employeeId}` | Bảo hiểm của nhân viên | `employeeId` |
| GET | `/insurances/new` | Form thêm bảo hiểm | `employeeId` (optional) |
| POST | `/insurances/save` | Lưu bảo hiểm | Form data |
| GET | `/insurances/edit/{id}` | Form sửa bảo hiểm | `id` |
| GET | `/insurances/delete/{id}` | Xóa bảo hiểm | `id` |

#### **Học vấn (Educations)**

**Base Path**: `/educations`

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/educations/employee/{employeeId}` | Học vấn của nhân viên | `employeeId` |
| GET | `/educations/new` | Form thêm học vấn | `employeeId` (optional) |
| POST | `/educations/save` | Lưu học vấn | Form data |
| GET | `/educations/edit/{id}` | Form sửa học vấn | `id` |
| GET | `/educations/delete/{id}` | Xóa học vấn | `id` |

#### **Lịch sử công việc (Work Histories)**

**Base Path**: `/workhistories`

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/workhistories/employee/{employeeId}` | Lịch sử công việc của nhân viên | `employeeId` |
| GET | `/workhistories/new` | Form thêm lịch sử | `employeeId` (optional) |
| POST | `/workhistories/save` | Lưu lịch sử | Form data |
| GET | `/workhistories/edit/{id}` | Form sửa lịch sử | `id` |
| GET | `/workhistories/delete/{id}` | Xóa lịch sử | `id` |

#### **Lương (Salaries) - Legacy**

**Base Path**: `/salaries`

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/salaries` | Danh sách lương | - |
| GET | `/salaries/new` | Form thêm lương | - |
| POST | `/salaries/save` | Lưu lương | Form data |
| GET | `/salaries/edit/{id}` | Form sửa lương | `id` |
| GET | `/salaries/delete/{id}` | Xóa lương | `id` |

#### **Chấm công (Attendances) - Legacy**

**Base Path**: `/attendances`

| Method | Endpoint | Mô tả | Parameters |
|--------|----------|-------|------------|
| GET | `/attendances` | Danh sách chấm công | - |
| GET | `/attendances/new` | Form thêm chấm công | - |
| POST | `/attendances/save` | Lưu chấm công | Form data |
| GET | `/attendances/edit/{id}` | Form sửa chấm công | `id` |
| GET | `/attendances/delete/{id}` | Xóa chấm công | `id` |

---

## 🔐 AUTHENTICATION

### OAuth2 với Keycloak

1. **Login URL**: `http://localhost:8081/oauth2/authorization/keycloak`
2. **Keycloak Server**: `http://localhost:8080`
3. **Realm**: `hrm-realm`
4. **Client ID**: `hrm-client`

### Test API qua Postman:

1. **Đăng nhập qua browser** để lấy session cookie
2. **Copy cookie** từ browser (F12 → Application → Cookies)
3. **Thêm cookie vào Postman**:
   - Headers → Add → `Cookie: JSESSIONID=xxx`
4. **Gửi request** đến endpoint

### Test với Postman OAuth2:

1. **Authorization Type**: OAuth 2.0
2. **Grant Type**: Authorization Code
3. **Auth URL**: `http://localhost:8080/realms/hrm-realm/protocol/openid-connect/auth`
4. **Access Token URL**: `http://localhost:8080/realms/hrm-realm/protocol/openid-connect/token`
5. **Client ID**: `hrm-client`
6. **Client Secret**: `9y6KSH0BWwlJykDjKS5EA9pvXvcoGEW6`
7. **Scope**: `openid profile email roles`

---

## 📝 THÊM SWAGGER/OPENAPI

### Tại sao cần Swagger?

- ✅ Tự động tạo tài liệu API
- ✅ Test API trực tiếp trên UI
- ✅ Hỗ trợ OAuth2 authentication
- ✅ Generate client code

### Cách thêm Swagger:

#### 1. Thêm dependency vào `pom.xml`:

```xml
<!-- SpringDoc OpenAPI (Swagger) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

#### 2. Tạo config class:

```java
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("HRM System API")
                .version("1.0.0")
                .description("Human Resource Management System API Documentation"))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")));
    }
}
```

#### 3. Truy cập Swagger UI:

- **Swagger UI**: `http://localhost:8081/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8081/v3/api-docs`

#### 4. Chuyển đổi Controller sang REST API:

Để có REST API JSON, cần:
- Thay `@Controller` bằng `@RestController`
- Thay `String` return type bằng `ResponseEntity<T>`
- Sử dụng `@ResponseBody` hoặc trả về DTO objects

---

## 📊 THỐNG KÊ

- **Tổng số endpoints**: ~144 endpoints
- **Controllers**: 17 controllers
- **Modules chính**:
  - Employee: 15 endpoints
  - HR: 35+ endpoints
  - Manager: 9 endpoints
  - Admin: 20+ endpoints
  - CRUD modules: 65+ endpoints

---

## 🔗 LIÊN KẾT HỮU ÍCH

- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [SpringDoc OpenAPI](https://springdoc.org/)
- [Postman OAuth2 Guide](https://learning.postman.com/docs/sending-requests/authorization/oauth-20/)

---

**📅 Cập nhật lần cuối**: 2025-01-XX  
**👤 Người tạo**: HRM System Development Team

