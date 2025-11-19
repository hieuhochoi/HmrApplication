package com.example.hrmapplication.controller;

import com.example.hrmapplication.dto.WorkHistoryRequest;
import com.example.hrmapplication.mapper.WorkHistoryMapper;
import com.example.hrmapplication.service.DepartmentService;
import com.example.hrmapplication.service.EmployeeService;
import com.example.hrmapplication.service.PositionService;
import com.example.hrmapplication.service.WorkHistoryService;
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
@RequestMapping("/workhistories")
@RequiredArgsConstructor
public class WorkHistoryController {

    private final WorkHistoryService workHistoryService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PositionService positionService;
    private final WorkHistoryMapper workHistoryMapper;

    @GetMapping("/employee/{employeeId}")
    public String listByEmployee(@PathVariable Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        var workHistories = workHistoryService.findByEmployeeId(employeeId)
                .stream()
                .map(workHistoryMapper::toResponse)
                .collect(Collectors.toList());

        model.addAttribute("workHistories", workHistories);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        return "workhistory/list";
    }

    @GetMapping("/new")
    public String showForm(@RequestParam Long employeeId, Model model) {
        employeeService.findById(employeeId);
        WorkHistoryRequest request = new WorkHistoryRequest();
        request.setEmployeeId(employeeId);
        model.addAttribute("workHistory", request);
        addSelectionData(model);
        addEmployeeContext(employeeId, model);
        return "workhistory/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("workHistory") WorkHistoryRequest workHistoryRequest,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        Long employeeId = workHistoryRequest.getEmployeeId();

        if (result.hasErrors()) {
            addSelectionData(model);
            addEmployeeContext(employeeId, model);
            return "workhistory/form";
        }

        try {
            var employee = employeeService.findById(employeeId);
            var department = workHistoryRequest.getDepartmentId() != null
                    ? departmentService.findById(workHistoryRequest.getDepartmentId())
                    : null;
            var position = workHistoryRequest.getPositionId() != null
                    ? positionService.findById(workHistoryRequest.getPositionId())
                    : null;

            if (workHistoryRequest.getId() != null) {
                var existing = workHistoryService.findById(workHistoryRequest.getId());
                workHistoryMapper.updateEntity(existing, workHistoryRequest);
                existing.setEmployee(employee);
                existing.setDepartment(department);
                existing.setPosition(position);
                workHistoryService.save(existing);
            } else {
                var workHistory = workHistoryMapper.toEntity(workHistoryRequest);
                workHistory.setEmployee(employee);
                workHistory.setDepartment(department);
                workHistory.setPosition(position);
                workHistoryService.save(workHistory);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Lưu quá trình công tác thành công.");
            return "redirect:/workhistories/employee/" + employeeId;
        } catch (DataIntegrityViolationException ex) {
            log.warn("Lỗi dữ liệu khi lưu quá trình công tác cho nhân viên [{}]", employeeId, ex);
            model.addAttribute("errorMessage", "Thông tin quá trình công tác đã tồn tại hoặc không hợp lệ.");
            addSelectionData(model);
            addEmployeeContext(employeeId, model);
            return "workhistory/form";
        } catch (Exception ex) {
            log.error("Không thể lưu quá trình công tác cho nhân viên [{}]: {}", employeeId, ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình lưu. Vui lòng thử lại sau.");
            addSelectionData(model);
            addEmployeeContext(employeeId, model);
            return "workhistory/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        var workHistory = workHistoryService.findById(id);
        WorkHistoryRequest request = workHistoryMapper.toRequest(workHistory);
        model.addAttribute("workHistory", request);
        addSelectionData(model);
        addEmployeeContext(request.getEmployeeId(), model);
        return "workhistory/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        var workHistory = workHistoryService.findById(id);
        Long employeeId = workHistory.getEmployee().getId();
        try {
            workHistoryService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa quá trình công tác thành công.");
        } catch (DataIntegrityViolationException ex) {
            log.warn("Không thể xóa quá trình công tác [{}] do ràng buộc dữ liệu", id, ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa quá trình công tác vì đang được sử dụng.");
        }
        return "redirect:/workhistories/employee/" + employeeId;
    }

    private void addSelectionData(Model model) {
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("positions", positionService.findAll());
    }

    private void addEmployeeContext(Long employeeId, Model model) {
        var employee = employeeService.findById(employeeId);
        model.addAttribute("employeeName", employee.getFullName());
        model.addAttribute("employeeId", employeeId);
    }
}
