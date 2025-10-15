package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Contract;
import com.example.hrmapplication.service.ContractService;
import com.example.hrmapplication.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final EmployeeService employeeService;

    @GetMapping("/employee/{employeeId}")
    public String listByEmployee(@PathVariable Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        model.addAttribute("contracts", contractService.findByEmployeeId(employeeId));
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        return "contract/list";
    }

    @GetMapping("/new")
    public String showForm(@RequestParam Long employeeId, Model model) {
        Contract contract = new Contract();
        contract.setEmployee(employeeService.findById(employeeId));
        model.addAttribute("contract", contract);
        return "contract/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Contract contract) {
        contractService.save(contract);
        return "redirect:/contracts/employee/" + contract.getEmployee().getId();
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("contract", contractService.findById(id));
        return "contract/form";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("contract", contractService.findById(id));
        return "contract/detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Contract contract = contractService.findById(id);
        Long employeeId = contract.getEmployee().getId();
        contractService.delete(id);
        return "redirect:/contracts/employee/" + employeeId;
    }
}