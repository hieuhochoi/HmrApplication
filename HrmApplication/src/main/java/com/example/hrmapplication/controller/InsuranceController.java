package com.example.hrmapplication.controller;

import com.example.hrmapplication.dto.InsuranceRequest;
import com.example.hrmapplication.mapper.InsuranceMapper;
import com.example.hrmapplication.service.EmployeeService;
import com.example.hrmapplication.service.InsuranceService;
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
@RequestMapping("/insurances")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;
    private final EmployeeService employeeService;
    private final InsuranceMapper insuranceMapper;

    @GetMapping("/employee/{employeeId}")
    public String listByEmployee(@PathVariable Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        var insurances = insuranceService.findByEmployeeId(employeeId)
                .stream()
                .map(insuranceMapper::toResponse)
                .collect(Collectors.toList());
        model.addAttribute("insurances", insurances);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        return "insurance/list";
    }

    @GetMapping("/new")
    public String showForm(@RequestParam Long employeeId, Model model) {
        employeeService.findById(employeeId); // ensure employee exists
        InsuranceRequest request = new InsuranceRequest();
        request.setEmployeeId(employeeId);
        model.addAttribute("insurance", request);
        addEmployeeContext(employeeId, model);
        return "insurance/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("insurance") InsuranceRequest insuranceRequest,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Long employeeId = insuranceRequest.getEmployeeId();

        if (result.hasErrors()) {
            addEmployeeContext(employeeId, model);
            return "insurance/form";
        }

        try {
            var employee = employeeService.findById(employeeId);
            if (insuranceRequest.getId() != null) {
                var existing = insuranceService.findById(insuranceRequest.getId());
                insuranceMapper.updateEntity(existing, insuranceRequest);
                existing.setEmployee(employee);
                insuranceService.save(existing);
            } else {
                var insurance = insuranceMapper.toEntity(insuranceRequest);
                insurance.setEmployee(employee);
                insuranceService.save(insurance);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Lưu thông tin bảo hiểm thành công.");
            return "redirect:/insurances/employee/" + employeeId;
        } catch (DataIntegrityViolationException ex) {
            log.warn("Trùng số bảo hiểm [{}]", insuranceRequest.getInsuranceNumber(), ex);
            model.addAttribute("errorMessage", "Số bảo hiểm đã tồn tại. Vui lòng kiểm tra lại.");
            addEmployeeContext(employeeId, model);
            return "insurance/form";
        } catch (Exception ex) {
            log.error("Không thể lưu bảo hiểm [{}]: {}", insuranceRequest.getInsuranceNumber(), ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình lưu. Vui lòng thử lại sau.");
            addEmployeeContext(employeeId, model);
            return "insurance/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        var insurance = insuranceService.findById(id);
        var request = insuranceMapper.toRequest(insurance);
        model.addAttribute("insurance", request);
        addEmployeeContext(request.getEmployeeId(), model);
        return "insurance/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var insurance = insuranceService.findById(id);
        Long employeeId = insurance.getEmployee().getId();
        try {
            insuranceService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bảo hiểm thành công.");
        } catch (DataIntegrityViolationException ex) {
            log.warn("Không thể xóa bảo hiểm [{}] do ràng buộc dữ liệu", id, ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa bảo hiểm vì đang được sử dụng.");
        }
        return "redirect:/insurances/employee/" + employeeId;
    }

    private void addEmployeeContext(Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        model.addAttribute("employeeId", employeeId);
    }
}
