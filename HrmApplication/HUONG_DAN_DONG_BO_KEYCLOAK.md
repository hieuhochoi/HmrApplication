# Hướng dẫn đồng bộ Keycloak với Database

## Vấn đề hiện tại

Tất cả các trường `keycloak_user_id` trong bảng `employees` đang là **NULL**. Điều này khiến hệ thống không thể liên kết user đăng nhập từ Keycloak với Employee record trong database.

## Giải pháp

### Cách 1: Sử dụng tính năng tự động đồng bộ (Khuyến nghị)

1. **Truy cập trang đồng bộ:**
   ```
   http://localhost:8081/admin/keycloak/sync
   ```

2. **Xem trạng thái:**
   - Xem danh sách employees chưa liên kết
   - Xem danh sách Keycloak users
   - Kiểm tra số lượng chưa liên kết

3. **Click "Đồng bộ ngay":**
   - Hệ thống sẽ tự động liên kết employees với Keycloak users theo email
   - **Yêu cầu:** Email trong Keycloak phải khớp chính xác với email trong database

4. **Kiểm tra kết quả:**
   ```sql
   SELECT id, full_name, email, keycloak_user_id 
   FROM employees 
   WHERE keycloak_user_id IS NOT NULL;
   ```

### Cách 2: Sử dụng API Test (JSON Response)

1. **Truy cập endpoint:**
   ```
   http://localhost:8081/admin/keycloak/sync/test
   ```

2. **Xem kết quả JSON:**
   - `keycloakUsersCount`: Số lượng users trong Keycloak
   - `employeesCount`: Số lượng employees trong database
   - `keycloakUsers`: Danh sách users từ Keycloak (id, username, email)
   - `employees`: Danh sách employees (id, fullName, email, keycloakUserId)
   - `syncedCount`: Số lượng employees đã được đồng bộ

### Cách 3: Đồng bộ thủ công qua SQL

1. **Lấy User ID từ Keycloak:**
   - Truy cập: http://localhost:8080
   - Đăng nhập: admin/admin
   - Vào Realm "hrm-realm" → Users
   - Click vào từng user → Copy "User ID" (UUID)

2. **So sánh email:**
   - Kiểm tra email trong Keycloak
   - So sánh với email trong database

3. **Cập nhật SQL:**
   ```sql
   UPDATE employees 
   SET keycloak_user_id = 'KEYCLOAK_USER_ID_HERE'
   WHERE email = 'user@example.com';
   ```

4. **Sử dụng script có sẵn:**
   - Mở file: `sync_keycloak_manual.sql`
   - Thay thế các giá trị `KEYCLOAK_USER_ID_X` bằng User ID thực tế
   - Chạy các câu lệnh UPDATE

## Kiểm tra Keycloak Users

### Bước 1: Truy cập Keycloak Admin
```
URL: http://localhost:8080
Username: admin
Password: admin
```

### Bước 2: Vào Realm
1. Chọn Realm: **hrm-realm**
2. Vào menu: **Users**
3. Xem danh sách users

### Bước 3: Lấy User ID
1. Click vào user cần liên kết
2. Copy **User ID** (UUID dạng: `12345678-1234-1234-1234-123456789abc`)
3. Copy **Email** để so sánh với database

## Kiểm tra Database

### Xem employees chưa liên kết:
```sql
SELECT id, full_name, email, keycloak_user_id
FROM employees
WHERE keycloak_user_id IS NULL
  AND email IS NOT NULL;
```

### Xem employees đã liên kết:
```sql
SELECT id, full_name, email, keycloak_user_id
FROM employees
WHERE keycloak_user_id IS NOT NULL;
```

## Lưu ý quan trọng

1. **Email phải khớp chính xác:**
   - Email trong Keycloak phải giống hệt email trong database
   - Phân biệt chữ hoa/thường (tùy cấu hình)
   - Không có khoảng trắng thừa

2. **Keycloak và Database là 2 hệ thống riêng:**
   - Keycloak có database riêng (thường là PostgreSQL hoặc MySQL)
   - Ứng dụng Spring Boot có database riêng (`hrm_db`)
   - Cần liên kết thủ công hoặc tự động

3. **Tự động liên kết khi đăng nhập:**
   - Hệ thống đã có tính năng tự động liên kết khi user đăng nhập
   - Chỉ hoạt động nếu email khớp và `keycloak_user_id` đang NULL

## Troubleshooting

### Vấn đề: Không có employees nào được đồng bộ

**Nguyên nhân có thể:**
- Email trong Keycloak không khớp với email trong database
- Keycloak chưa có users nào
- Lỗi kết nối đến Keycloak

**Giải pháp:**
1. Kiểm tra email trong Keycloak và database
2. Sử dụng API test để xem chi tiết: `/admin/keycloak/sync/test`
3. Kiểm tra cấu hình Keycloak trong `application.properties`

### Vấn đề: Lỗi kết nối Keycloak

**Kiểm tra:**
1. Keycloak đang chạy: http://localhost:8080
2. Cấu hình trong `application.properties`:
   ```properties
   keycloak.server-url=http://localhost:8080
   keycloak.realm=hrm-realm
   keycloak.admin-client-id=admin-cli
   keycloak.admin-username=admin
   keycloak.admin-password=admin
   ```

## Sau khi đồng bộ thành công

1. **Kiểm tra lại database:**
   ```sql
   SELECT COUNT(*) FROM employees WHERE keycloak_user_id IS NOT NULL;
   ```

2. **Test đăng nhập:**
   - Đăng nhập bằng tài khoản Keycloak
   - Kiểm tra xem có thấy thông tin Employee không

3. **Kiểm tra dashboard:**
   - User đăng nhập sẽ thấy dashboard tương ứng với role
   - Không còn lỗi "Không tìm thấy thông tin nhân viên"

