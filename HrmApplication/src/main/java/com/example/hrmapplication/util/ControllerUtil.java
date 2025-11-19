package com.example.hrmapplication.util;

import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Utility class cho các controller để giảm code lặp lại
 */
public class ControllerUtil {

    /**
     * Thêm thông báo lỗi vào model
     */
    public static void addError(Model model, String message) {
        model.addAttribute("error", message);
    }

    /**
     * Thêm thông báo lỗi vào redirect attributes
     */
    public static void addError(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("errorMessage", message);
    }

    /**
     * Thêm thông báo thành công vào redirect attributes
     */
    public static void addSuccess(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("successMessage", message);
    }

    /**
     * Xử lý exception và thêm thông báo lỗi
     */
    public static void handleException(Model model, Exception e, String defaultMessage) {
        String errorMessage = e.getMessage() != null && !e.getMessage().isEmpty() 
                ? e.getMessage() 
                : defaultMessage;
        addError(model, errorMessage);
    }

    /**
     * Xử lý exception và thêm thông báo lỗi vào redirect
     */
    public static void handleException(RedirectAttributes redirectAttributes, Exception e, String defaultMessage) {
        String errorMessage = e.getMessage() != null && !e.getMessage().isEmpty() 
                ? e.getMessage() 
                : defaultMessage;
        addError(redirectAttributes, errorMessage);
    }
}

