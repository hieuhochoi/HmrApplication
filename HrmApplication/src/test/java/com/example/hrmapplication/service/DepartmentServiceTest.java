package com.example.hrmapplication.service;

import com.example.hrmapplication.entity.Department;
import com.example.hrmapplication.exception.ResourceNotFoundException;
import com.example.hrmapplication.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void shouldReturnDepartmentWhenFound() {
        Department department = new Department();
        department.setId(1L);
        department.setDepartmentName("IT");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        Department result = departmentService.findById(1L);

        assertThat(result.getDepartmentName()).isEqualTo("IT");
    }

    @Test
    void shouldThrowResourceNotFoundWhenDepartmentMissing() {
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Không tìm thấy phòng ban");
    }
}

