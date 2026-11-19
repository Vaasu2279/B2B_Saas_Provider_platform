package com.saas.B2Bplatfrom.employee;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
        Long departmentId,
        String employeeCode,
        String fullName,
        String email,
        String jobTitle,
        BigDecimal salary,
        LocalDate joinDate) {}
