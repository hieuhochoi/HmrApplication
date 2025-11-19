package com.example.hrmapplication.exception;

/**
 * Ngoại lệ biểu diễn lỗi nghiệp vụ, có thể hiển thị cho người dùng cuối.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

