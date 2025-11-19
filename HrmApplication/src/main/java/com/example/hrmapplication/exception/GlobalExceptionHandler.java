package com.example.hrmapplication.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * Xử lý ngoại lệ tập trung cho toàn bộ ứng dụng MVC.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_ERROR_VIEW = "error/500";
    private static final String NOT_FOUND_VIEW = "error/404";

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found at [{}]: {}", request.getRequestURI(), ex.getMessage());
        ModelAndView mav = new ModelAndView(NOT_FOUND_VIEW);
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("message", ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler({BusinessException.class})
    public ModelAndView handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.error("Business error at [{}]: {}", request.getRequestURI(), ex.getMessage());
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        mav.setStatus(HttpStatus.BAD_REQUEST);
        mav.addObject("message", ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ModelAndView handleValidationException(Exception ex, HttpServletRequest request, Model model) {
        log.debug("Validation error at [{}]: {}", request.getRequestURI(), ex.getMessage());
        ModelAndView mav = new ModelAndView("error/validation");
        mav.setStatus(HttpStatus.BAD_REQUEST);
        mav.addObject("message", "Dữ liệu gửi lên không hợp lệ. Vui lòng kiểm tra lại.");
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnexpectedException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at [{}]", request.getRequestURI(), ex);
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("message", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
        mav.addObject("path", request.getRequestURI());
        return mav;
    }
}

