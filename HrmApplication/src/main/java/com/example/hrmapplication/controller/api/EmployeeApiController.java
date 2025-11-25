package com.example.hrmapplication.controller.api;

import com.example.hrmapplication.dto.EmployeeResponse;
import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.repository.EmployeeRepository;
import com.example.hrmapplication.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/employee")
@Tag(name = "Employee API", description = "API quản lý thông tin nhân viên")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearer-jwt")
public class EmployeeApiController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/profile")
    @Operation(summary = "Lấy thông tin cá nhân", description = "Lấy thông tin nhân viên hiện tại đang đăng nhập")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên")
    })
    public ResponseEntity<EmployeeResponse> getMyProfile() {
        try {
            // TODO: Lấy employee từ SecurityContext
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @Operation(summary = "Danh sách nhân viên", description = "Lấy danh sách tất cả nhân viên (có phân trang)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng phần tử mỗi trang", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Tìm kiếm theo tên")
            @RequestParam(required = false) String search,
            @Parameter(description = "Lọc theo trạng thái (ACTIVE, INACTIVE)")
            @RequestParam(required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Employee> employees;
            
            if (search != null && !search.trim().isEmpty()) {
                employees = employeeRepository.findByFullNameContainingIgnoreCase(search.trim(), pageable);
            } else if (status != null && !status.isEmpty()) {
                employees = employeeRepository.findByStatus(status, pageable);
            } else {
                employees = employeeRepository.findAll(pageable);
            }
            
            Page<EmployeeResponse> response = employees.map(emp -> 
                EmployeeResponse.builder()
                    .id(emp.getId())
                    .fullName(emp.getFullName())
                    .email(emp.getEmail())
                    .phone(emp.getPhone())
                    .status(emp.getStatus())
                    .build()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết nhân viên", description = "Lấy thông tin chi tiết của một nhân viên theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = EmployeeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên")
    })
    public ResponseEntity<EmployeeResponse> getEmployeeById(
            @Parameter(description = "ID nhân viên", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Employee employee = employeeService.findById(id);
            if (employee == null) {
                return ResponseEntity.notFound().build();
            }
            
            EmployeeResponse response = EmployeeResponse.builder()
                    .id(employee.getId())
                    .fullName(employee.getFullName())
                    .email(employee.getEmail())
                    .phone(employee.getPhone())
                    .status(employee.getStatus())
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

