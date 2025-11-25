package com.example.hrmapplication.controller.api;

import com.example.hrmapplication.dto.SalaryResponse;
import com.example.hrmapplication.entity.Salary;
import com.example.hrmapplication.repository.SalaryRepository;
import com.example.hrmapplication.service.SalaryService;
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
@RequestMapping("/api/salaries")
@Tag(name = "Salary API", description = "API quản lý lương")
@PreAuthorize("isAuthenticated()")
@SecurityRequirement(name = "bearer-jwt")
public class SalaryApiController {

    @Autowired
    private SalaryService salaryService;

    @Autowired
    private SalaryRepository salaryRepository;

    @GetMapping
    @Operation(summary = "Danh sách lương", description = "Lấy danh sách lương (có phân trang và lọc)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập")
    })
    public ResponseEntity<Page<SalaryResponse>> getAllSalaries(
            @Parameter(description = "Số trang (bắt đầu từ 0)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số lượng phần tử mỗi trang", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Lọc theo tháng (1-12)", example = "10")
            @RequestParam(required = false) Integer month,
            @Parameter(description = "Lọc theo năm", example = "2025")
            @RequestParam(required = false) Integer year,
            @Parameter(description = "Lọc theo trạng thái (PENDING, PAID, CANCELLED)")
            @RequestParam(required = false) String status,
            @Parameter(description = "Lọc theo ID nhân viên")
            @RequestParam(required = false) Long employeeId) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Salary> salariesPage = salaryRepository.findAll(pageable);
            
            // Apply filters in memory (có thể tối ưu bằng cách thêm vào repository)
            List<Salary> filtered = salariesPage.getContent().stream()
                    .filter(s -> month == null || s.getMonth() == month)
                    .filter(s -> year == null || s.getYear() == year)
                    .filter(s -> status == null || (s.getStatus() != null && s.getStatus().equals(status)))
                    .filter(s -> employeeId == null || (s.getEmployee() != null && s.getEmployee().getId().equals(employeeId)))
                    .collect(Collectors.toList());
            
            Page<SalaryResponse> response = new org.springframework.data.domain.PageImpl<>(
                    filtered.stream()
                            .map(salary -> SalaryResponse.builder()
                                    .id(salary.getId())
                                    .employeeId(salary.getEmployee() != null ? salary.getEmployee().getId() : null)
                                    .employeeName(salary.getEmployee() != null ? salary.getEmployee().getFullName() : null)
                                    .month(salary.getMonth())
                                    .year(salary.getYear())
                                    .baseSalary(salary.getBaseSalary())
                                    .allowance(salary.getAllowance())
                                    .bonus(salary.getBonus())
                                    .overtime(salary.getOvertime())
                                    .totalSalary(salary.getTotalSalary())
                                    .status(salary.getStatus())
                                    .build())
                            .collect(Collectors.toList()),
                    pageable,
                    filtered.size()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết lương", description = "Lấy thông tin chi tiết của một bản ghi lương theo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công",
                    content = @Content(schema = @Schema(implementation = SalaryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bản ghi lương")
    })
    public ResponseEntity<SalaryResponse> getSalaryById(
            @Parameter(description = "ID bản ghi lương", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Salary salary = salaryService.findById(id);
            if (salary == null) {
                return ResponseEntity.notFound().build();
            }
            
            SalaryResponse response = SalaryResponse.builder()
                    .id(salary.getId())
                    .employeeId(salary.getEmployee() != null ? salary.getEmployee().getId() : null)
                    .employeeName(salary.getEmployee() != null ? salary.getEmployee().getFullName() : null)
                    .month(salary.getMonth())
                    .year(salary.getYear())
                    .baseSalary(salary.getBaseSalary())
                    .allowance(salary.getAllowance())
                    .bonus(salary.getBonus())
                    .overtime(salary.getOvertime())
                    .advance(salary.getAdvance())
                    .deduction(salary.getDeduction())
                    .totalSalary(salary.getTotalSalary())
                    .status(salary.getStatus())
                    .paymentDate(salary.getPaymentDate())
                    .build();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/my")
    @Operation(summary = "Lương của tôi", description = "Lấy danh sách lương của nhân viên hiện tại đang đăng nhập")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Thành công"),
            @ApiResponse(responseCode = "401", description = "Chưa đăng nhập")
    })
    public ResponseEntity<List<SalaryResponse>> getMySalaries(
            @Parameter(description = "Lọc theo tháng (1-12)", example = "10")
            @RequestParam(required = false) Integer month,
            @Parameter(description = "Lọc theo năm", example = "2025")
            @RequestParam(required = false) Integer year) {
        try {
            // TODO: Lấy employee từ SecurityContext
            // List<Salary> salaries = salaryService.findByEmployeeId(currentEmployeeId);
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

