package com.example.hrmapplication.controller.api;

import com.example.hrmapplication.dto.DepartmentResponse;
import com.example.hrmapplication.entity.Department;
import com.example.hrmapplication.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Department API", description = "API quản lý phòng ban")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearer-jwt")
public class DepartmentApiController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "Danh sách phòng ban", description = "Lấy danh sách tất cả phòng ban")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        try {
            List<Department> departments = departmentService.findAll();
            List<DepartmentResponse> response = departments.stream()
                    .map(dept -> DepartmentResponse.builder()
                            .id(dept.getId())
                            .departmentCode(dept.getDepartmentCode())
                            .departmentName(dept.getDepartmentName())
                            .description(dept.getDescription())
                            .build())
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết phòng ban", description = "Lấy thông tin chi tiết của một phòng ban theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = DepartmentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy phòng ban")
    })
    public ResponseEntity<DepartmentResponse> getDepartmentById(
            @Parameter(description = "ID phòng ban", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Department department = departmentService.findById(id);
            if (department == null) {
                return ResponseEntity.notFound().build();
            }
            
            DepartmentResponse response = DepartmentResponse.builder()
                    .id(department.getId())
                    .departmentCode(department.getDepartmentCode())
                    .departmentName(department.getDepartmentName())
                    .description(department.getDescription())
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

