package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Education;
import com.example.hrmapplication.service.EducationService;
import com.example.hrmapplication.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final EmployeeService employeeService;

    @GetMapping("/employee/{employeeId}")
    public String listByEmployee(@PathVariable Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        model.addAttribute("educations", educationService.findByEmployeeId(employeeId));
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        return "education/list";
    }

    @GetMapping("/new")
    public String showForm(@RequestParam Long employeeId, Model model) {
        Education education = new Education();
        education.setEmployee(employeeService.findById(employeeId));
        model.addAttribute("education", education);
        return "education/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Education education) {
        educationService.save(education);
        return "redirect:/educations/employee/" + education.getEmployee().getId();
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("education", educationService.findById(id));
        return "education/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Education education = educationService.findById(id);
        Long employeeId = education.getEmployee().getId();
        educationService.delete(id);
        return "redirect:/educations/employee/" + employeeId;
    }
}