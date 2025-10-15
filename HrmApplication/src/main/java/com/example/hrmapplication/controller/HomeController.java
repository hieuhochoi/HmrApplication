package com.example.hrmapplication.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal != null) {
            model.addAttribute("username", principal.getPreferredUsername());
            model.addAttribute("email", principal.getEmail());
            model.addAttribute("fullName", principal.getFullName());

            // Lấy roles
            var roles = principal.getClaimAsStringList("roles");
            model.addAttribute("roles", roles);
        }
        return "index";
    }
}