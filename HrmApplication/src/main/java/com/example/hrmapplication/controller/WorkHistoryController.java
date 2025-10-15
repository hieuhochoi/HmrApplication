package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.WorkHistory;
import com.example.hrmapplication.service.WorkHistoryService;
import com.example.hrmapplication.service.EmployeeService;
import com.example.hrmapplication.service.DepartmentService;
import com.example.hrmapplication.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/workhistories")
@RequiredArgsConstructor
public class WorkHistoryController {

    private final WorkHistoryService workHistoryService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PositionService positionService;

    @GetMapping("/employee/{employeeId}")
    public String listByEmployee(@PathVariable Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        model.addAttribute("workHistories", workHistoryService.findByEmployeeId(employeeId));
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        return "workhistory/list";
    }

    @GetMapping("/new")
    public String showForm(@RequestParam Long employeeId, Model model) {
        WorkHistory workHistory = new WorkHistory();
        workHistory.setEmployee(employeeService.findById(employeeId));
        model.addAttribute("workHistory", workHistory);
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("positions", positionService.findAll());
        return "workhistory/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute WorkHistory workHistory) {
        workHistoryService.save(workHistory);
        return "redirect:/workhistories/employee/" + workHistory.getEmployee().getId();
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("workHistory", workHistoryService.findById(id));
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("positions", positionService.findAll());
        return "workhistory/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        WorkHistory workHistory = workHistoryService.findById(id);
        Long employeeId = workHistory.getEmployee().getId();
        workHistoryService.delete(id);
        return "redirect:/workhistories/employee/" + employeeId;
    }
}