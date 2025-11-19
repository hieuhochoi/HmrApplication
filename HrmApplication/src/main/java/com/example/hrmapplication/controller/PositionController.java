package com.example.hrmapplication.controller;

import com.example.hrmapplication.dto.PositionRequest;
import com.example.hrmapplication.entity.Position;
import com.example.hrmapplication.mapper.PositionMapper;
import com.example.hrmapplication.service.PositionService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;
    private final PositionMapper positionMapper;

    @GetMapping
    public String list(Model model) {
        var positions = positionService.findAll()
                .stream()
                .map(positionMapper::toResponse)
                .collect(Collectors.toList());
        model.addAttribute("positions", positions);
        return "position/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("position", new PositionRequest());
        return "position/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("position") PositionRequest positionRequest,
                       BindingResult result,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "position/form";
        }

        try {
            if (positionRequest.getId() != null) {
                Position existing = positionService.findById(positionRequest.getId());
                positionMapper.updateEntity(existing, positionRequest);
                positionService.save(existing);
            } else {
                Position position = positionMapper.toEntity(positionRequest);
                positionService.save(position);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Lưu chức vụ thành công.");
            return "redirect:/positions";
        } catch (DataIntegrityViolationException ex) {
            log.warn("Trùng mã chức vụ [{}]", positionRequest.getPositionCode(), ex);
            model.addAttribute("errorMessage", "Mã chức vụ đã tồn tại. Vui lòng kiểm tra lại.");
            return "position/form";
        } catch (Exception ex) {
            log.error("Không thể lưu chức vụ [{}]: {}", positionRequest.getPositionCode(), ex.getMessage(), ex);
            model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình lưu. Vui lòng thử lại sau.");
            return "position/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        var position = positionService.findById(id);
        PositionRequest request = positionMapper.toRequest(position);
        model.addAttribute("position", request);
        return "position/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            positionService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa chức vụ thành công.");
        } catch (DataIntegrityViolationException ex) {
            log.warn("Không thể xóa chức vụ [{}] do ràng buộc dữ liệu", id, ex);
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa chức vụ vì đang được sử dụng.");
        }
        return "redirect:/positions";
    }
}
