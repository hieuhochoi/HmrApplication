package com.example.hrmapplication.exception;

/**
 * Ngoại lệ chung cho các trường hợp không tìm thấy dữ liệu.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

