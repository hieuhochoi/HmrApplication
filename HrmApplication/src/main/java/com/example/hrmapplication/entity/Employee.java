package com.example.hrmapplication.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Họ tên không được để trống")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "Giới tính không được để trống")
    private String gender;

    @NotNull(message = "Ngày sinh không được để trống")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String address;

    @NotBlank(message = "Số CCCD không được để trống")
    @Column(name = "citizen_id", unique = true)
    private String citizenId;

    @Column(name = "birth_place")
    private String birthPlace;

    @Column(name = "current_address")
    private String currentAddress;

    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải có 10 chữ số")
    private String phone;

    @Email(message = "Email không hợp lệ")
    private String email;

    private String status = "ACTIVE"; // ACTIVE, INACTIVE, RESIGNED

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Education> educations;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Insurance> insurances;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Contract> contracts;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<WorkHistory> workHistories;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Attendance> attendances;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Salary> salaries;

    @Column(name = "created_at", updatable = false)
    private LocalDate createdAt = LocalDate.now();

    @Column(name = "updated_at")
    private LocalDate updatedAt = LocalDate.now();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDate.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = LocalDate.now();
        }
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDate.now();
    }
}