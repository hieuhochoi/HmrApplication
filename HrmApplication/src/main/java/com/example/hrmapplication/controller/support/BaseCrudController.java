package com.example.hrmapplication.controller.support;

import com.example.hrmapplication.service.CrudService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.function.Supplier;

/**
 * Lớp cơ sở cho các controller CRUD đơn giản nhằm giảm lặp mã và chuẩn hóa luồng xử lý.
 *
 * @param <T> kiểu thực thể được quản lý
 */
public abstract class BaseCrudController<T> {

    protected abstract CrudService<T, Long> getService();

    protected abstract Supplier<T> getEntitySupplier();

    protected abstract String getListView();

    protected abstract String getFormView();

    protected abstract String getListModelAttribute();

    protected abstract String getFormModelAttribute();

    protected abstract String getRedirectToList();

    @GetMapping
    public String list(Model model) {
        model.addAttribute(getListModelAttribute(), getService().findAll());
        return getListView();
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute(getFormModelAttribute(), getEntitySupplier().get());
        return getFormView();
    }

    @PostMapping("/save")
    public String save(T entity) {
        getService().save(entity);
        return getRedirectToList();
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute(getFormModelAttribute(), getService().findById(id));
        return getFormView();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        getService().delete(id);
        return getRedirectToList();
    }
}

