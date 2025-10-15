package com.example.hrmapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "positions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position_code", unique = true, nullable = false)
    private String positionCode;

    @Column(name = "position_name", nullable = false)
    private String positionName;

    private String description;

    @Column(name = "base_salary")
    private Double baseSalary;

    @OneToMany(mappedBy = "position", cascade = CascadeType.ALL)
    private List<WorkHistory> workHistories;
}