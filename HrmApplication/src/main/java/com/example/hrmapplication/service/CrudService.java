package com.example.hrmapplication.service;

import java.util.List;

/**
 * Định nghĩa các thao tác CRUD cơ bản cho tầng service.
 *
 * @param <T>  kiểu thực thể
 * @param <ID> kiểu định danh
 */
public interface CrudService<T, ID> {

    List<T> findAll();

    T findById(ID id);

    T save(T entity);

    void delete(ID id);
}

