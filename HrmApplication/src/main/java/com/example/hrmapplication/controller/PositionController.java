package com.example.hrmapplication.controller;

import com.example.hrmapplication.entity.Position;
import com.example.hrmapplication.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("positions", positionService.findAll());
        return "position/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("position", new Position());
        return "position/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Position position) {
        positionService.save(position);
        return "redirect:/positions";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("position", positionService.findById(id));
        return "position/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        positionService.delete(id);
        return "redirect:/positions";
    }
}