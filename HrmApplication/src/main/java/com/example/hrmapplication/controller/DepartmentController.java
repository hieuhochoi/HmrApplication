package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Department;
import com.example.hrmapplication.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("departments", departmentService.findAll());
        return "department/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("department", new Department());
        return "department/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Department department) {
        departmentService.save(department);
        return "redirect:/departments";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("department", departmentService.findById(id));
        return "department/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        departmentService.delete(id);
        return "redirect:/departments";
    }
}