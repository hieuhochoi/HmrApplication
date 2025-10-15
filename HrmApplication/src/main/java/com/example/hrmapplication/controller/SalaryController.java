package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Salary;
import com.example.hrmapplication.service.SalaryService;
import com.example.hrmapplication.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;
    private final EmployeeService employeeService;

    @GetMapping
    public String list(@RequestParam(required = false) Integer month,
                       @RequestParam(required = false) Integer year,
                       Model model) {
        if (month != null && year != null) {
            model.addAttribute("salaries", salaryService.findByMonthYear(month, year));
        }
        return "salary/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("salary", new Salary());
        model.addAttribute("employees", employeeService.findAll());
        return "salary/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Salary salary) {
        salaryService.save(salary);
        return "redirect:/salaries";
    }
}