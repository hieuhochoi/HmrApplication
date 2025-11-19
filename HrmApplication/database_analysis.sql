-- ============================================
-- PHÂN TÍCH DATABASE VÀ KIỂM TRA KEYCLOAK INTEGRATION
-- ============================================

-- 1. Kiểm tra Employees chưa được liên kết với Keycloak
SELECT 
    id, 
    full_name, 
    email, 
    keycloak_user_id,
    status,
    CASE 
        WHEN keycloak_user_id IS NULL THEN 'CHƯA LIÊN KẾT'
        ELSE 'ĐÃ LIÊN KẾT'
    END AS link_status
FROM employees
ORDER BY id;

-- 2. Thống kê tổng quan
SELECT 
    COUNT(*) AS total_employees,
    COUNT(keycloak_user_id) AS linked_employees,
    COUNT(*) - COUNT(keycloak_user_id) AS unlinked_employees
FROM employees;

-- 3. Kiểm tra employees có email nhưng chưa liên kết
SELECT 
    id, 
    full_name, 
    email, 
    phone,
    status
FROM employees
WHERE email IS NOT NULL 
  AND email != ''
  AND keycloak_user_id IS NULL
ORDER BY id;

-- 4. Kiểm tra dữ liệu hợp đồng
SELECT 
    e.id AS employee_id,
    e.full_name,
    e.email,
    c.contract_number,
    c.contract_type,
    c.salary,
    c.status AS contract_status
FROM employees e
LEFT JOIN contracts c ON e.id = c.employee_id
WHERE e.status = 'ACTIVE'
ORDER BY e.id;

-- 5. Kiểm tra employees có phòng ban và chức vụ
SELECT 
    e.id,
    e.full_name,
    e.email,
    d.department_name,
    p.position_name,
    e.status
FROM employees e
LEFT JOIN departments d ON e.current_department_id = d.id
LEFT JOIN positions p ON e.current_position_id = p.id
ORDER BY e.id;

-- 6. Kiểm tra chấm công
SELECT 
    e.full_name,
    a.work_date,
    a.check_in,
    a.check_out,
    a.status,
    a.work_hours
FROM attendances a
JOIN employees e ON a.employee_id = e.id
ORDER BY a.work_date DESC, e.full_name;

-- 7. Kiểm tra lương
SELECT 
    e.full_name,
    s.month,
    s.year,
    s.base_salary,
    s.total_salary,
    s.status
FROM salaries s
JOIN employees e ON s.employee_id = e.id
ORDER BY s.year DESC, s.month DESC, e.full_name;

-- ============================================
-- SCRIPT ĐỒNG BỘ KEYCLOAK (CẦN CẬP NHẬT THỦ CÔNG)
-- ============================================

-- Cập nhật keycloak_user_id cho employee (thay thế 'KEYCLOAK_USER_ID_HERE' và email)
-- UPDATE employees 
-- SET keycloak_user_id = 'KEYCLOAK_USER_ID_HERE'
-- WHERE email = 'user@example.com';

-- ============================================
-- KIỂM TRA DỮ LIỆU THIẾU
-- ============================================

-- Employees chưa có hợp đồng
SELECT e.id, e.full_name, e.email
FROM employees e
LEFT JOIN contracts c ON e.id = c.employee_id
WHERE c.id IS NULL
  AND e.status = 'ACTIVE';

-- Employees chưa có phòng ban
SELECT id, full_name, email
FROM employees
WHERE current_department_id IS NULL
  AND status = 'ACTIVE';

-- Employees chưa có chức vụ
SELECT id, full_name, email
FROM employees
WHERE current_position_id IS NULL
  AND status = 'ACTIVE';

