package com.example.hrmapplication.controller;

import com.example.hrmapplication.dto.SalaryRequest;
import com.example.hrmapplication.mapper.SalaryMapper;
import com.example.hrmapplication.service.EmployeeService;
import com.example.hrmapplication.service.SalaryService;
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
@RequestMapping("/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;
    private final EmployeeService employeeService;
    private final SalaryMapper salaryMapper;

    @GetMapping
    public String list(@RequestParam(required = false) Integer month,
                       @RequestParam(required = false) Integer year,
                       Model model) {
        var salaries = salaryService.findAll();
        if (month != null && year != null) {
            salaries = salaryService.findByMonthYear(month, year);
        }
        model.addAttribute("salaries", salaries.stream().map(salaryMapper::toResponse).collect(Collectors.toList()));
        model.addAttribute("selectedMonth", month);
        model.addAttribute("selectedYear", year);
        return "salary/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        SalaryRequest request = new SalaryRequest();
        model.addAttribute("salary", request);
        addSelectionData(model);
        return "salary/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        var salary = salaryService.findById(id);
        SalaryRequest request = salaryMapper.toRequest(salary);
        model.addAttribute("salary", request);
        addSelectionData(model);
        return "salary/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("salary") SalaryRequest salaryRequest,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            addSelectionData(model);
            return "salary/form";
        }

        try {
            var employee = employeeService.findById(salaryRequest.getEmployeeId());
            if (salaryRequest.getId() != null) {
                var existing = salaryService.findById(salaryRequest.getId());
                salaryMapper.updateEntity(existing, salaryRequest);
                existing.setEmployee(employee);
           	    salaryService.save(existing);
            } else {
                var salary = salaryMapper.toEntity(salaryRequest);
                salary.setEmployee(employee);
                salaryService.save(salary);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Lưu bảng lương thành công.");
            return "redirect:/salaries";
        } catch (DataIntegrityViolationException ex) {
            log.warn("Trùng dữ liệu bảng lương cho nhân viên [{}] tháng [{}]/[{}]",
                    salaryRequest.getEmployeeId(), salaryRequest.getMonth(), salaryRequest.getYear(), ex);
            model.addAttribute("errorMessage", "Bảng lương cho nhân viên này và tháng/năm này đã tồn tại.");
            addSelectionData(model);
            return "salary/form";
        } catch (Exception ex) {
            log.error("Không thể lưu bảng lương: {}", ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình lưu. Vui lòng thử lại sau.");
            addSelectionData(model);
            return "salary/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            salaryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bảng lương thành công.");
        } catch (DataIntegrityViolationException ex) {
            log.warn("Không thể xóa bảng lương [{}] do ràng buộc dữ liệu", id, ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa bảng lương vì đang được sử dụng.");
        }
        return "redirect:/salaries";
    }

    private void addSelectionData(Model model) {
        model.addAttribute("employees", employeeService.findAll());
    }
}
