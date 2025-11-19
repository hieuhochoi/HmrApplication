package com.example.hrmapplication.controller;

import com.example.hrmapplication.dto.EducationRequest;
import com.example.hrmapplication.mapper.EducationMapper;
import com.example.hrmapplication.service.EducationService;
import com.example.hrmapplication.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;
    private final EmployeeService employeeService;
    private final EducationMapper educationMapper;

    @GetMapping("/employee/{employeeId}")
    public String listByEmployee(@PathVariable Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        var educations = educationService.findByEmployeeId(employeeId)
                .stream()
                .map(educationMapper::toResponse)
                .collect(Collectors.toList());

        model.addAttribute("educations", educations);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        return "education/list";
    }

    @GetMapping("/new")
    public String showForm(@RequestParam Long employeeId, Model model) {
        employeeService.findById(employeeId);
        EducationRequest request = new EducationRequest();
        request.setEmployeeId(employeeId);
        model.addAttribute("education", request);
        addEmployeeContext(employeeId, model);
        return "education/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("education") EducationRequest educationRequest,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Long employeeId = educationRequest.getEmployeeId();

        if (result.hasErrors()) {
            addEmployeeContext(employeeId, model);
            return "education/form";
        }

        try {
            var employee = employeeService.findById(employeeId);
            if (educationRequest.getId() != null) {
                var existing = educationService.findById(educationRequest.getId());
                educationMapper.updateEntity(existing, educationRequest);
                existing.setEmployee(employee);
                educationService.save(existing);
            } else {
                var education = educationMapper.toEntity(educationRequest);
                education.setEmployee(employee);
                educationService.save(education);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Lưu thông tin trình độ thành công.");
            return "redirect:/educations/employee/" + employeeId;
        } catch (DataIntegrityViolationException ex) {
            log.warn("Lỗi dữ liệu khi lưu trình độ cho nhân viên [{}]", employeeId, ex);
            model.addAttribute("errorMessage", "Thông tin trình độ đã tồn tại. Vui lòng kiểm tra lại.");
            addEmployeeContext(employeeId, model);
            return "education/form";
        } catch (Exception ex) {
            log.error("Không thể lưu trình độ cho nhân viên [{}]: {}", employeeId, ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình lưu. Vui lòng thử lại sau.");
            addEmployeeContext(employeeId, model);
            return "education/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        var education = educationService.findById(id);
        EducationRequest request = educationMapper.toRequest(education);
        model.addAttribute("education", request);
        addEmployeeContext(request.getEmployeeId(), model);
        return "education/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var education = educationService.findById(id);
        Long employeeId = education.getEmployee().getId();
        try {
            educationService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa trình độ thành công.");
        } catch (DataIntegrityViolationException ex) {
            log.warn("Không thể xóa trình độ [{}] do ràng buộc dữ liệu", id, ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa trình độ vì đang được sử dụng.");
        }
        return "redirect:/educations/employee/" + employeeId;
    }

    private void addEmployeeContext(Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        model.addAttribute("employeeId", employeeId);
    }
}
