package com.example.hrmapplication.controller;

import com.example.hrmapplication.dto.AttendanceRequest;
import com.example.hrmapplication.mapper.AttendanceMapper;
import com.example.hrmapplication.service.AttendanceService;
import com.example.hrmapplication.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;
    private final AttendanceMapper attendanceMapper;

    @GetMapping
    public String list(@RequestParam(required = false) Long employeeId, Model model) {
        var attendances = attendanceService.findByEmployeeId(employeeId);

        model.addAttribute("attendances",
                attendances.stream().map(attendanceMapper::toResponse).collect(Collectors.toList()));
        model.addAttribute("employees", employeeService.findAll());
        model.addAttribute("selectedEmployeeId", employeeId);
        return "attendance/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("attendance", new AttendanceRequest());
        addSelectionData(model);
        return "attendance/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        var attendance = attendanceService.findById(id);
        var request = attendanceMapper.toRequest(attendance);
        model.addAttribute("attendance", request);
        addSelectionData(model);
        return "attendance/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("attendance") AttendanceRequest attendanceRequest,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            addSelectionData(model);
            return "attendance/form";
        }

        try {
            var employee = employeeService.findById(attendanceRequest.getEmployeeId());
            if (attendanceRequest.getId() != null) {
                var existing = attendanceService.findById(attendanceRequest.getId());
                attendanceMapper.updateEntity(existing, attendanceRequest);
                existing.setEmployee(employee);
                attendanceService.save(existing);
            } else {
                var attendance = attendanceMapper.toEntity(attendanceRequest);
                attendance.setEmployee(employee);
                attendanceService.save(attendance);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Lưu thông tin chấm công thành công.");
            return "redirect:/attendances";
        } catch (DataIntegrityViolationException ex) {
            log.warn("Trùng dữ liệu chấm công cho nhân viên [{}] ngày [{}]",
                    attendanceRequest.getEmployeeId(), attendanceRequest.getWorkDate(), ex);
            model.addAttribute("errorMessage", "Chấm công cho nhân viên này tại ngày này đã tồn tại.");
            addSelectionData(model);
            return "attendance/form";
        } catch (Exception ex) {
            log.error("Không thể lưu chấm công: {}", ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình lưu. Vui lòng thử lại sau.");
            addSelectionData(model);
            return "attendance/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            attendanceService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chấm công thành công.");
        } catch (DataIntegrityViolationException ex) {
            log.warn("Không thể xóa chấm công [{}] do ràng buộc dữ liệu", id, ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa chấm công vì đang được sử dụng.");
        }
        return "redirect:/attendances";
    }

    private void addSelectionData(Model model) {
        model.addAttribute("employees", employeeService.findAll());
    }
}
