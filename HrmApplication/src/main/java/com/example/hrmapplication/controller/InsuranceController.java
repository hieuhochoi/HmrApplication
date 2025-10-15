package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Insurance;
import com.example.hrmapplication.service.InsuranceService;
import com.example.hrmapplication.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/insurances")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;
    private final EmployeeService employeeService;

    @GetMapping("/employee/{employeeId}")
    public String listByEmployee(@PathVariable Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        model.addAttribute("insurances", insuranceService.findByEmployeeId(employeeId));
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        return "insurance/list";
    }

    @GetMapping("/new")
    public String showForm(@RequestParam Long employeeId, Model model) {
        Insurance insurance = new Insurance();
        insurance.setEmployee(employeeService.findById(employeeId));
        model.addAttribute("insurance", insurance);
        return "insurance/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Insurance insurance) {
        insuranceService.save(insurance);
        return "redirect:/insurances/employee/" + insurance.getEmployee().getId();
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("insurance", insuranceService.findById(id));
        return "insurance/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Insurance insurance = insuranceService.findById(id);
        Long employeeId = insurance.getEmployee().getId();
        insuranceService.delete(id);
        return "redirect:/insurances/employee/" + employeeId;
    }
}