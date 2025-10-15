package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Attendance;
import com.example.hrmapplication.service.AttendanceService;
import com.example.hrmapplication.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    @GetMapping
    public String list(@RequestParam(required = false) Long employeeId, Model model) {
        if (employeeId != null) {
            model.addAttribute("attendances", attendanceService.findByEmployeeId(employeeId));
        }
        model.addAttribute("employees", employeeService.findAll());
        return "attendance/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("attendance", new Attendance());
        model.addAttribute("employees", employeeService.findAll());
        return "attendance/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Attendance attendance) {
        attendanceService.save(attendance);
        return "redirect:/attendances";
    }
}
