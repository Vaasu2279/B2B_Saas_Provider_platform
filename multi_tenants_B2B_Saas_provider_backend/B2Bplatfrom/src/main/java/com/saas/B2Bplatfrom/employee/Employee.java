package com.saas.B2Bplatfrom.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_id", "employee_code"}))
@Getter @Setter @NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "employee_code", nullable = false, length = 30)
    private String employeeCode;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(length = 150)
    private String email;

    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Column(precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Version
    private Long version;
}