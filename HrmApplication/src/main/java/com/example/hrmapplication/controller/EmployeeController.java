package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Employee;
import com.example.hrmapplication.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public String list(Model model, @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("employees", employeeService.search(keyword));
        } else {
            model.addAttribute("employees", employeeService.findAll());
        }
        return "employee/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "employee/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("employee") Employee employee,
                       BindingResult result,
                       Model model) {
        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            return "employee/form";
        }

        try {
            employeeService.save(employee);
            return "redirect:/employees";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "employee/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        try {
            Employee employee = employeeService.findById(id);
            model.addAttribute("employee", employee);
            return "employee/form";
        } catch (Exception e) {
            return "redirect:/employees";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        try {
            Employee employee = employeeService.findById(id);
            model.addAttribute("employee", employee);
            return "employee/detail";
        } catch (Exception e) {
            return "redirect:/employees";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        try {
            employeeService.delete(id);
        } catch (Exception e) {
            // Log error or show error message
        }
        return "redirect:/employees";
    }
}