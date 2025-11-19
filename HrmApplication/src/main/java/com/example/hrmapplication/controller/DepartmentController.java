package com.example.hrmapplication.controller;

import com.example.hrmapplication.dto.DepartmentRequest;
import com.example.hrmapplication.entity.Department;
import com.example.hrmapplication.mapper.DepartmentMapper;
import com.example.hrmapplication.service.DepartmentService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DepartmentMapper departmentMapper;

    @GetMapping
    public String list(Model model) {
        var departments = departmentService.findAll()
                .stream()
                .map(departmentMapper::toResponse)
                .collect(Collectors.toList());
        model.addAttribute("departments", departments);
        return "department/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("department", new DepartmentRequest());
        return "department/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("department") DepartmentRequest departmentRequest,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "department/form";
        }

        try {
            if (departmentRequest.getId() != null) {
                Department existing = departmentService.findById(departmentRequest.getId());
                departmentMapper.updateEntity(existing, departmentRequest);
                departmentService.save(existing);
            } else {
                Department department = departmentMapper.toEntity(departmentRequest);
                departmentService.save(department);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Lưu phòng ban thành công.");
            return "redirect:/departments";
        } catch (DataIntegrityViolationException ex) {
            log.warn("Trùng mã phòng ban [{}]", departmentRequest.getDepartmentCode(), ex);
            model.addAttribute("errorMessage", "Mã phòng ban đã tồn tại. Vui lòng kiểm tra lại.");
            return "department/form";
        } catch (Exception ex) {
            log.error("Không thể lưu phòng ban [{}]: {}", departmentRequest.getDepartmentCode(), ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình lưu. Vui lòng thử lại sau.");
            return "department/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        var department = departmentService.findById(id);
        DepartmentRequest request = departmentMapper.toRequest(department);
        model.addAttribute("department", request);
        return "department/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa phòng ban thành công.");
        } catch (DataIntegrityViolationException ex) {
            log.warn("Không thể xóa phòng ban [{}] do ràng buộc dữ liệu", id, ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa phòng ban vì đang được sử dụng.");
        }
        return "redirect:/departments";
    }
}