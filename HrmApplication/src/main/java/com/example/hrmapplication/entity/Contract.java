package com.example.hrmapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "contract_number", unique = true)
    private String contractNumber;

    @Column(name = "signed_date")
    private LocalDate signedDate;

    @Column(name = "contract_type")
    private String contractType;

    private Integer duration;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private Double salary;

    private String status = "ACTIVE"; // ACTIVE, EXPIRED, TERMINATED

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }
}