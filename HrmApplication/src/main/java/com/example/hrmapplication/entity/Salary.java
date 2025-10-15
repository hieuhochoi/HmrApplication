package com.example.hrmapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "salaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private Integer month;

    private Integer year;

    @Column(name = "base_salary")
    private Double baseSalary;

    private Double allowance = 0.0;

    private Double bonus = 0.0;

    private Double overtime = 0.0;

    private Double advance = 0.0;

    private Double deduction = 0.0;

    @Column(name = "total_salary")
    private Double totalSalary;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    private String status = "PENDING"; // PENDING, PAID, CANCELLED

    @PrePersist
    @PreUpdate
    public void calculateTotalSalary() {
        this.totalSalary = (this.baseSalary != null ? this.baseSalary : 0.0) +
                (this.allowance != null ? this.allowance : 0.0) +
                (this.bonus != null ? this.bonus : 0.0) +
                (this.overtime != null ? this.overtime : 0.0) -
                (this.advance != null ? this.advance : 0.0) -
                (this.deduction != null ? this.deduction : 0.0);

        if (this.status == null) {
            this.status = "PENDING";
        }
    }
}