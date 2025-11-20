package com.example.hrmapplication.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller để xử lý các redirect cho các URL cũ hoặc tương thích
 */
@Controller
public class RedirectController {

    /**
     * Redirect từ /employees sang /hr/employees
     * Để tương thích với các link cũ trong template
     */
    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public String redirectEmployees() {
        return "redirect:/hr/employees";
    }
}

