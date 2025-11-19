package com.example.hrmapplication.controller;

import com.example.hrmapplication.dto.ContractRequest;
import com.example.hrmapplication.dto.ContractResponse;
import com.example.hrmapplication.entity.Contract;
import com.example.hrmapplication.mapper.ContractMapper;
import com.example.hrmapplication.service.ContractService;
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
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final EmployeeService employeeService;
    private final ContractMapper contractMapper;

    @GetMapping("/employee/{employeeId}")
    public String listByEmployee(@PathVariable Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        var contracts = contractService.findByEmployeeId(employeeId)
                .stream()
                .map(contractMapper::toResponse)
                .collect(Collectors.toList());

        model.addAttribute("contracts", contracts);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        return "contract/list";
    }

    @GetMapping("/new")
    public String showForm(@RequestParam Long employeeId, Model model) {
        employeeService.findById(employeeId); // ensure tồn tại

        ContractRequest contractRequest = new ContractRequest();
        contractRequest.setEmployeeId(employeeId);
        model.addAttribute("contract", contractRequest);
        addEmployeeContext(employeeId, model);
        return "contract/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("contract") ContractRequest contractRequest,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Long employeeId = contractRequest.getEmployeeId();

        if (result.hasErrors()) {
            addEmployeeContext(employeeId, model);
            return "contract/form";
        }

        try {
            var employee = employeeService.findById(employeeId);
            if (contractRequest.getId() != null) {
                Contract existing = contractService.findById(contractRequest.getId());
                contractMapper.updateEntity(existing, contractRequest);
                existing.setEmployee(employee);
                contractService.save(existing);
            } else {
                Contract contract = contractMapper.toEntity(contractRequest);
                contract.setEmployee(employee);
                contractService.save(contract);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Lưu hợp đồng thành công.");
            return "redirect:/contracts/employee/" + employeeId;
        } catch (DataIntegrityViolationException ex) {
            log.warn("Trùng số hợp đồng [{}]", contractRequest.getContractNumber(), ex);
            model.addAttribute("errorMessage", "Số hợp đồng đã tồn tại. Vui lòng kiểm tra lại.");
            addEmployeeContext(employeeId, model);
            return "contract/form";
        } catch (Exception ex) {
            log.error("Không thể lưu hợp đồng [{}]: {}", contractRequest.getContractNumber(), ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình lưu. Vui lòng thử lại sau.");
            addEmployeeContext(employeeId, model);
            return "contract/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Contract contract = contractService.findById(id);
        ContractRequest request = contractMapper.toRequest(contract);
        model.addAttribute("contract", request);
        addEmployeeContext(request.getEmployeeId(), model);
        return "contract/form";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ContractResponse contract = contractMapper.toResponse(contractService.findById(id));
        model.addAttribute("contract", contract);
        return "contract/detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Contract contract = contractService.findById(id);
        Long employeeId = contract.getEmployee().getId();
        try {
            contractService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa hợp đồng thành công.");
        } catch (DataIntegrityViolationException ex) {
            log.warn("Không thể xóa hợp đồng [{}] do ràng buộc dữ liệu", id, ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa hợp đồng vì đang được sử dụng.");
        }
        return "redirect:/contracts/employee/" + employeeId;
    }

    private void addEmployeeContext(Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        model.addAttribute("employeeId", employeeId);
    }
}