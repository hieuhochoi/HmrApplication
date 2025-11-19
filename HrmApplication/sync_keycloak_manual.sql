-- ============================================
-- SCRIPT ĐỒNG BỘ KEYCLOAK THỦ CÔNG
-- ============================================
-- Sử dụng script này nếu tính năng tự động đồng bộ không hoạt động
-- 
-- HƯỚNG DẪN:
-- 1. Truy cập Keycloak Admin UI: http://localhost:8080
-- 2. Đăng nhập: admin/admin
-- 3. Vào Realm "hrm-realm" → Users
-- 4. Click vào từng user và copy "User ID" (UUID)
-- 5. So sánh email trong Keycloak với email trong database
-- 6. Cập nhật keycloak_user_id cho employee tương ứng
-- ============================================

-- Ví dụ: Cập nhật keycloak_user_id cho employee có email 'vana@hrm.com'
-- Thay 'YOUR_KEYCLOAK_USER_ID_HERE' bằng User ID từ Keycloak
-- UPDATE employees 
-- SET keycloak_user_id = 'YOUR_KEYCLOAK_USER_ID_HERE'
-- WHERE email = 'vana@hrm.com';

-- ============================================
-- KIỂM TRA TRƯỚC KHI CẬP NHẬT
-- ============================================

-- Xem tất cả employees và email
SELECT 
    id, 
    full_name, 
    email, 
    keycloak_user_id,
    CASE 
        WHEN keycloak_user_id IS NULL THEN 'CHƯA LIÊN KẾT'
        ELSE 'ĐÃ LIÊN KẾT'
    END AS status
FROM employees
ORDER BY id;

-- Xem employees chưa liên kết
SELECT 
    id, 
    full_name, 
    email, 
    phone,
    status
FROM employees
WHERE keycloak_user_id IS NULL
  AND email IS NOT NULL
  AND email != ''
ORDER BY id;

-- ============================================
-- CẬP NHẬT TỪNG EMPLOYEE (THAY ĐỔI GIÁ TRỊ)
-- ============================================

-- Employee ID 1: Nguyễn Văn Aa (vana@hrm.com)
-- UPDATE employees 
-- SET keycloak_user_id = 'KEYCLOAK_USER_ID_1'
-- WHERE id = 1 AND email = 'vana@hrm.com';

-- Employee ID 2: Trần Thị B (thib@hrm.com)
-- UPDATE employees 
-- SET keycloak_user_id = 'KEYCLOAK_USER_ID_2'
-- WHERE id = 2 AND email = 'thib@hrm.com';

-- Employee ID 3: Lê Văn C (vanc@hrm.com)
-- UPDATE employees 
-- SET keycloak_user_id = 'KEYCLOAK_USER_ID_3'
-- WHERE id = 3 AND email = 'vanc@hrm.com';

-- Employee ID 4: test 1 (hieu@gmail.com)
-- UPDATE employees 
-- SET keycloak_user_id = 'KEYCLOAK_USER_ID_4'
-- WHERE id = 4 AND email = 'hieu@gmail.com';

-- Employee ID 5: Nguyễn Văn 123 (AAAA123@gmail.com)
-- UPDATE employees 
-- SET keycloak_user_id = 'KEYCLOAK_USER_ID_5'
-- WHERE id = 5 AND email = 'AAAA123@gmail.com';

-- Employee ID 33: test 2 2 (bb@gmail.com)
-- UPDATE employees 
-- SET keycloak_user_id = 'KEYCLOAK_USER_ID_33'
-- WHERE id = 33 AND email = 'bb@gmail.com';

-- Employee ID 34: Luân Đức Hiếu (Hieu1234@gmail.com)
-- UPDATE employees 
-- SET keycloak_user_id = 'KEYCLOAK_USER_ID_34'
-- WHERE id = 34 AND email = 'Hieu1234@gmail.com';

-- ============================================
-- KIỂM TRA SAU KHI CẬP NHẬT
-- ============================================

-- Xem kết quả sau khi cập nhật
SELECT 
    id, 
    full_name, 
    email, 
    keycloak_user_id,
    CASE 
        WHEN keycloak_user_id IS NULL THEN 'CHƯA LIÊN KẾT'
        ELSE 'ĐÃ LIÊN KẾT'
    END AS status
FROM employees
ORDER BY id;

-- Đếm số lượng đã liên kết
SELECT 
    COUNT(*) AS total,
    COUNT(keycloak_user_id) AS linked,
    COUNT(*) - COUNT(keycloak_user_id) AS unlinked
FROM employees;

