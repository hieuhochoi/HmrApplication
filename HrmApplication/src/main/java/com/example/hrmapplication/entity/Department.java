package com.example.hrmapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "department_code", unique = true, nullable = false)
    private String departmentCode;

    @Column(name = "department_name", nullable = false)
    private String departmentName;

    private String description;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<WorkHistory> workHistories;
}