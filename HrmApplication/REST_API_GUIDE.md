# 🚀 HƯỚNG DẪN REST API - HRM SYSTEM

## 📋 Tổng quan

Dự án HRM System hiện có **2 loại API**:

1. **Web Controllers** (`@Controller`): Trả về HTML views (Thymeleaf)
   - Base paths: `/hr`, `/employee`, `/admin`, `/manager`
   - Sử dụng cho web interface

2. **REST API Controllers** (`@RestController`): Trả về JSON
   - Base paths: `/api/employee`, `/api/departments`, `/api/salaries`
   - Sử dụng cho mobile apps, third-party integrations, Swagger testing

---

## 🔗 REST API Endpoints

### **Employee API** (`/api/employee`)

| Method | Endpoint | Mô tả | Quyền |
|--------|----------|-------|-------|
| GET | `/api/employee/profile` | Thông tin cá nhân | Authenticated |
| GET | `/api/employee` | Danh sách nhân viên (phân trang) | ADMIN, HR |
| GET | `/api/employee/{id}` | Chi tiết nhân viên | Authenticated |

**Query Parameters** (GET `/api/employee`):
- `page`: Số trang (default: 0)
- `size`: Số lượng mỗi trang (default: 20)
- `search`: Tìm kiếm theo tên
- `status`: Lọc theo trạng thái (ACTIVE, INACTIVE)

**Ví dụ**:
```bash
GET /api/employee?page=0&size=20&search=Nguyễn&status=ACTIVE
```

**Response**:
```json
{
  "content": [
    {
      "id": 1,
      "fullName": "Nguyễn Văn A",
      "email": "nguyenvana@example.com",
      "phone": "0123456789",
      "status": "ACTIVE"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

---

### **Department API** (`/api/departments`)

| Method | Endpoint | Mô tả | Quyền |
|--------|----------|-------|-------|
| GET | `/api/departments` | Danh sách phòng ban | Authenticated |
| GET | `/api/departments/{id}` | Chi tiết phòng ban | Authenticated |

**Response**:
```json
[
  {
    "id": 1,
    "departmentCode": "IT",
    "departmentName": "Công nghệ thông tin",
    "description": "Phòng ban IT"
  }
]
```

---

### **Salary API** (`/api/salaries`)

| Method | Endpoint | Mô tả | Quyền |
|--------|----------|-------|-------|
| GET | `/api/salaries` | Danh sách lương (phân trang) | Authenticated |
| GET | `/api/salaries/{id}` | Chi tiết lương | Authenticated |
| GET | `/api/salaries/my` | Lương của tôi | Authenticated |

**Query Parameters** (GET `/api/salaries`):
- `page`: Số trang (default: 0)
- `size`: Số lượng mỗi trang (default: 20)
- `month`: Lọc theo tháng (1-12)
- `year`: Lọc theo năm
- `status`: Lọc theo trạng thái (PENDING, PAID, CANCELLED)
- `employeeId`: Lọc theo ID nhân viên

**Ví dụ**:
```bash
GET /api/salaries?month=10&year=2025&status=PAID
```

**Response**:
```json
{
  "content": [
    {
      "id": 1,
      "employeeId": 1,
      "employeeName": "Nguyễn Văn A",
      "month": 10,
      "year": 2025,
      "baseSalary": 10000000.0,
      "allowance": 500000.0,
      "bonus": 2000000.0,
      "overtime": 500000.0,
      "totalSalary": 13000000.0,
      "status": "PAID",
      "paymentDate": "2025-10-31"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

---

## 🔐 Authentication

Tất cả REST API endpoints yêu cầu authentication. Có 2 cách:

### Cách 1: Bearer JWT Token (Khuyến nghị)

1. **Lấy token từ Keycloak**:
```bash
POST http://localhost:8080/realms/hrm-realm/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password
&client_id=hrm-client
&client_secret=9y6KSH0BWwlJykDjKS5EA9pvXvcoGEW6
&username=your-username
&password=your-password
```

2. **Sử dụng token trong request**:
```bash
GET http://localhost:8081/api/employee
Authorization: Bearer <access_token>
```

### Cách 2: Session Cookie

1. Đăng nhập qua browser: `http://localhost:8081/oauth2/authorization/keycloak`
2. Copy cookie `JSESSIONID` từ browser
3. Gửi kèm cookie trong request:
```bash
GET http://localhost:8081/api/employee
Cookie: JSESSIONID=xxx
```

---

## 📝 Test API

### Qua Swagger UI (Khuyến nghị)

1. Truy cập: `http://localhost:8081/swagger-ui.html`
2. Click "Authorize" → Chọn "bearer-jwt" hoặc "oauth2-keycloak"
3. Đăng nhập và test các endpoints

### Qua Postman

1. **Tạo request mới**
2. **Method**: GET/POST/PUT/DELETE
3. **URL**: `http://localhost:8081/api/employee`
4. **Headers**:
   - `Authorization: Bearer <token>`
   - `Content-Type: application/json`
5. **Send**

### Qua cURL

```bash
# Lấy token
TOKEN=$(curl -X POST http://localhost:8080/realms/hrm-realm/protocol/openid-connect/token \
  -d "grant_type=password" \
  -d "client_id=hrm-client" \
  -d "client_secret=9y6KSH0BWwlJykDjKS5EA9pvXvcoGEW6" \
  -d "username=your-username" \
  -d "password=your-password" | jq -r '.access_token')

# Gọi API
curl -X GET http://localhost:8081/api/employee \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🎯 Response Codes

| Code | Mô tả |
|------|-------|
| 200 | Thành công |
| 201 | Tạo mới thành công |
| 400 | Bad Request (dữ liệu không hợp lệ) |
| 401 | Unauthorized (chưa đăng nhập) |
| 403 | Forbidden (không có quyền) |
| 404 | Not Found (không tìm thấy) |
| 500 | Internal Server Error |

---

## 📦 Response Format

### Success Response

```json
{
  "id": 1,
  "fullName": "Nguyễn Văn A",
  ...
}
```

### Error Response

```json
{
  "timestamp": "2025-01-XX",
  "status": 404,
  "error": "Not Found",
  "message": "Không tìm thấy nhân viên với ID: 999",
  "path": "/api/employee/999"
}
```

### Paginated Response

```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 100,
  "totalPages": 5,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false
}
```

---

## 🔄 So sánh Web Controllers vs REST API

| Tính năng | Web Controllers | REST API |
|-----------|----------------|----------|
| **Base Path** | `/hr`, `/employee`, etc. | `/api/...` |
| **Response** | HTML (Thymeleaf) | JSON |
| **Use Case** | Web interface | Mobile apps, integrations |
| **Authentication** | Session cookie | JWT Bearer token |
| **Swagger** | Hiển thị nhưng response là HTML | Hiển thị và test được JSON |

---

## 🚧 TODO - Các API cần thêm

- [ ] POST `/api/employee` - Tạo nhân viên mới
- [ ] PUT `/api/employee/{id}` - Cập nhật nhân viên
- [ ] DELETE `/api/employee/{id}` - Xóa nhân viên
- [ ] POST `/api/salaries` - Tạo lương mới
- [ ] PUT `/api/salaries/{id}` - Cập nhật lương
- [ ] POST `/api/salaries/{id}/approve` - Duyệt lương
- [ ] GET `/api/leave-requests` - Danh sách đơn nghỉ phép
- [ ] POST `/api/leave-requests` - Tạo đơn nghỉ phép
- [ ] GET `/api/attendances` - Danh sách chấm công

---

## 📚 Tài liệu tham khảo

- [API Documentation](./API_DOCUMENTATION.md) - Tài liệu đầy đủ tất cả endpoints
- [Swagger Guide](./HUONG_DAN_SWAGGER.md) - Hướng dẫn sử dụng Swagger UI
- [Spring REST Docs](https://spring.io/projects/spring-restdocs) - Tạo tài liệu API tự động

---

**📅 Cập nhật lần cuối**: 2025-01-XX  
**👤 Người tạo**: HRM System Development Team

