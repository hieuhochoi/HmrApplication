package com.example.hrmapplication.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "attendances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "work_date")
    private LocalDate workDate;

    private String shift;

    @Column(name = "check_in")
    private LocalTime checkIn;

    @Column(name = "check_out")
    private LocalTime checkOut;

    @Column(name = "work_hours")
    private Double workHours = 8.0;

    @Column(name = "overtime_hours")
    private Double overtimeHours = 0.0;

    @Column(name = "is_leave")
    private Boolean isLeave = false;

    @Column(name = "leave_reason")
    private String leaveReason;

    private String status = "PRESENT"; // PRESENT, ABSENT, LATE, etc.

    @PrePersist
    public void prePersist() {
        if (this.workHours == null) {
            this.workHours = 8.0;
        }
        if (this.overtimeHours == null) {
            this.overtimeHours = 0.0;
        }
        if (this.isLeave == null) {
            this.isLeave = false;
        }
        if (this.status == null) {
            this.status = "PRESENT";
        }
    }
}