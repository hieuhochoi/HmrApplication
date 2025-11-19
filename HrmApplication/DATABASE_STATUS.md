# Trạng thái Database hiện tại

## Phân tích từ SQL Dump

### 1. Employees (7 records)
- **Tất cả employees đều chưa được liên kết với Keycloak** (`keycloak_user_id = NULL`)
- Đây là **vấn đề chính** - người dùng đăng nhập bằng Keycloak nhưng không tìm thấy Employee record

**Danh sách Employees:**
1. Nguyễn Văn Aa (vana@hrm.com) - ACTIVE
2. Trần Thị B (thib@hrm.com) - ACTIVE
3. Lê Văn C (vanc@hrm.com) - ACTIVE
4. test 1 (hieu@gmail.com) - RESIGNED
5. Nguyễn Văn 123 (AAAA123@gmail.com) - INACTIVE
6. test 2 2 (bb@gmail.com) - ACTIVE
7. Luân Đức Hiếu (Hieu1234@gmail.com) - ACTIVE

### 2. Departments (4 records)
- IT, HR, ACC, SALE

### 3. Positions (4 records)
- DIR (Giám đốc), MGR (Trưởng phòng), LEAD (Team Leader), EMP (Nhân viên)

### 4. Contracts (3 records)
- Employee ID 1, 4, 34 có hợp đồng

### 5. Attendances (3 records)
- Employee ID 1 có 2 bản ghi
- Employee ID 34 có 1 bản ghi

### 6. Salaries (1 record)
- Employee ID 4 có 1 bản ghi lương tháng 10/2025

## Vấn đề cần giải quyết

### ⚠️ VẤN ĐỀ CHÍNH: Keycloak Integration

**Tất cả 7 employees đều có `keycloak_user_id = NULL`**

Điều này có nghĩa là:
- Khi user đăng nhập bằng Keycloak, hệ thống không tìm thấy Employee record tương ứng
- User sẽ thấy lỗi "Không tìm thấy thông tin nhân viên"

### Giải pháp:

#### Cách 1: Sử dụng tính năng tự động liên kết (Đã có sẵn)
- Khi đăng nhập, hệ thống tự động liên kết theo email
- **Yêu cầu:** Email trong Keycloak phải khớp với email trong database

#### Cách 2: Sử dụng tính năng đồng bộ (Đã tạo)
- Truy cập: `http://localhost:8081/admin/keycloak/sync`
- Click "Đồng bộ ngay" để tự động liên kết

#### Cách 3: Cập nhật thủ công qua SQL
```sql
-- Lấy Keycloak User ID từ Keycloak Admin UI
-- Sau đó cập nhật:
UPDATE employees 
SET keycloak_user_id = 'KEYCLOAK_USER_ID_HERE'
WHERE email = 'user@example.com';
```

## Hướng dẫn kiểm tra và sửa

### Bước 1: Kiểm tra Keycloak Users
1. Truy cập: http://localhost:8080
2. Đăng nhập: admin/admin
3. Vào Realm "hrm-realm" → Users
4. Ghi lại User ID và Email của mỗi user

### Bước 2: So sánh với Database
```sql
-- Xem employees và email
SELECT id, full_name, email, keycloak_user_id 
FROM employees 
WHERE email IS NOT NULL;
```

### Bước 3: Đồng bộ
- **Tự động:** Dùng tính năng sync tại `/admin/keycloak/sync`
- **Thủ công:** Cập nhật SQL như trên

## Dữ liệu cần bổ sung

### Employees chưa có phòng ban/chức vụ:
- Employee ID 1, 2, 3, 5, 33, 34: Chưa có `current_department_id` và `current_position_id`

### Employees chưa có hợp đồng:
- Employee ID 2, 3, 5, 33: Chưa có hợp đồng

## Khuyến nghị

1. **Ưu tiên:** Liên kết tất cả employees với Keycloak users
2. **Bổ sung:** Gán phòng ban và chức vụ cho employees
3. **Kiểm tra:** Đảm bảo email trong Keycloak khớp với email trong database

