# 📖 HƯỚNG DẪN SỬ DỤNG SWAGGER UI

## 🎯 Tổng quan

Swagger UI đã được tích hợp vào dự án HRM System để:
- ✅ Xem tài liệu API tự động
- ✅ Test API trực tiếp trên giao diện web
- ✅ Hỗ trợ OAuth2 authentication với Keycloak
- ✅ Export OpenAPI specification

---

## 🚀 Truy cập Swagger UI

### URL:
```
http://localhost:8081/swagger-ui.html
```

### OpenAPI JSON:
```
http://localhost:8081/v3/api-docs
```

---

## 🔐 Authentication

### Cách 1: OAuth2 (Keycloak) - Khuyến nghị

1. **Mở Swagger UI**: `http://localhost:8081/swagger-ui.html`

2. **Click nút "Authorize"** ở góc trên bên phải

3. **Chọn "oauth2-keycloak"** trong danh sách

4. **Điền thông tin**:
   - **Client ID**: `hrm-client`
   - **Client Secret**: `9y6KSH0BWwlJykDjKS5EA9pvXvcoGEW6`
   - **Authorization URL**: `http://localhost:8080/realms/hrm-realm/protocol/openid-connect/auth`
   - **Token URL**: `http://localhost:8080/realms/hrm-realm/protocol/openid-connect/token`
   - **Scopes**: `openid profile email roles`

5. **Click "Authorize"** → Sẽ redirect đến Keycloak login

6. **Đăng nhập** với tài khoản Keycloak

7. **Sau khi đăng nhập**, quay lại Swagger UI và token sẽ được tự động thêm vào requests

### Cách 2: Bearer JWT Token

1. **Lấy JWT token từ Keycloak**:

   **Option A: Qua Postman**
   ```
   POST http://localhost:8080/realms/hrm-realm/protocol/openid-connect/token
   Content-Type: application/x-www-form-urlencoded
   
   grant_type=password
   &client_id=hrm-client
   &client_secret=9y6KSH0BWwlJykDjKS5EA9pvXvcoGEW6
   &username=your-username
   &password=your-password
   ```

   **Option B: Qua cURL**
   ```bash
   curl -X POST http://localhost:8080/realms/hrm-realm/protocol/openid-connect/token \
     -d "grant_type=password" \
     -d "client_id=hrm-client" \
     -d "client_secret=9y6KSH0BWwlJykDjKS5EA9pvXvcoGEW6" \
     -d "username=your-username" \
     -d "password=your-password"
   ```

2. **Copy `access_token`** từ response

3. **Trong Swagger UI**, click "Authorize" → Chọn "bearer-jwt"

4. **Paste token** vào ô "Value" (format: `Bearer <token>` hoặc chỉ `<token>`)

5. **Click "Authorize"**

---

## 📝 Sử dụng Swagger UI

### 1. Xem danh sách API

- Tất cả endpoints được nhóm theo **tags** (modules)
- Click vào tag để mở rộng/xem các endpoints trong module đó

### 2. Test một endpoint

1. **Click vào endpoint** bạn muốn test (ví dụ: `GET /hr/employees`)

2. **Click nút "Try it out"**

3. **Điền parameters** (nếu có):
   - Path parameters
   - Query parameters
   - Request body (nếu là POST/PUT)

4. **Click "Execute"**

5. **Xem kết quả**:
   - **Response Code**: HTTP status code
   - **Response Body**: Nội dung response (HTML trong trường hợp này)
   - **Response Headers**: HTTP headers

### 3. Lưu ý quan trọng

⚠️ **Hiện tại tất cả endpoints trả về HTML (Thymeleaf views), không phải JSON!**

- Response sẽ là HTML page
- Để có REST API JSON, cần chuyển đổi controllers sang `@RestController`

---

## 🔧 Cấu hình nâng cao

### Tùy chỉnh Swagger UI

File cấu hình: `application.properties`

```properties
# Đường dẫn API docs
springdoc.api-docs.path=/v3/api-docs

# Đường dẫn Swagger UI
springdoc.swagger-ui.path=/swagger-ui.html

# Sắp xếp operations theo method
springdoc.swagger-ui.operationsSorter=method

# Sắp xếp tags theo alphabet
springdoc.swagger-ui.tagsSorter=alpha

# Bật "Try it out" mặc định
springdoc.swagger-ui.tryItOutEnabled=true

# Hiển thị thời gian request
springdoc.swagger-ui.display-request-duration=true

# Mở rộng mặc định
springdoc.swagger-ui.doc-expansion=none
```

### Tùy chỉnh trong code

File: `SwaggerConfig.java`

- Thay đổi thông tin API (title, description, version)
- Thêm servers (dev, staging, production)
- Cấu hình security schemes
- Thêm contact, license info

---

## 📦 Export OpenAPI Specification

### Download OpenAPI JSON:

```
http://localhost:8081/v3/api-docs
```

### Download OpenAPI YAML:

```
http://localhost:8081/v3/api-docs.yaml
```

### Sử dụng với các tools khác:

- **Postman**: Import OpenAPI spec để tạo collection
- **Insomnia**: Import OpenAPI spec
- **Code Generation**: Generate client code từ OpenAPI spec

---

## 🐛 Troubleshooting

### Lỗi: "Failed to fetch"

**Nguyên nhân**: Keycloak server chưa chạy hoặc không thể kết nối

**Giải pháp**:
1. Kiểm tra Keycloak đang chạy: `http://localhost:8080`
2. Kiểm tra realm `hrm-realm` đã được tạo
3. Kiểm tra client `hrm-client` đã được cấu hình đúng

### Lỗi: "401 Unauthorized"

**Nguyên nhân**: Chưa authenticate hoặc token hết hạn

**Giải pháp**:
1. Click "Authorize" và đăng nhập lại
2. Lấy token mới từ Keycloak
3. Kiểm tra token còn hiệu lực

### Lỗi: "403 Forbidden"

**Nguyên nhân**: User không có quyền truy cập endpoint

**Giải pháp**:
1. Kiểm tra role của user trong Keycloak
2. Đảm bảo user có role phù hợp (ADMIN, HR, MANAGER, EMPLOYEE)
3. Kiểm tra `@PreAuthorize` annotation trong controller

### Swagger UI không hiển thị

**Nguyên nhân**: Dependency chưa được tải hoặc compile lỗi

**Giải pháp**:
1. Chạy `mvn clean install`
2. Kiểm tra dependency trong `pom.xml`
3. Kiểm tra logs để xem lỗi cụ thể

---

## 📚 Tài liệu tham khảo

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)

---

## 🎓 Ví dụ sử dụng

### Test GET /hr/employees

1. Mở Swagger UI
2. Tìm endpoint `GET /hr/employees` trong tag "HR Controller"
3. Click "Try it out"
4. Điền query parameters (optional):
   - `search`: Tên nhân viên
   - `status`: ACTIVE/INACTIVE
   - `departmentId`: ID phòng ban
   - `page`: 0
   - `size`: 20
5. Click "Execute"
6. Xem response (HTML page)

### Test POST /hr/employees/save

1. Tìm endpoint `POST /hr/employees/save`
2. Click "Try it out"
3. Điền request body (form data):
   ```json
   {
     "fullName": "Nguyễn Văn A",
     "email": "nguyenvana@example.com",
     "phone": "0123456789",
     ...
   }
   ```
4. Click "Execute"
5. Xem response

---

**📅 Cập nhật lần cuối**: 2025-01-XX  
**👤 Người tạo**: HRM System Development Team

