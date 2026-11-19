package com.saas.B2Bplatfrom.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByTenantId(Long tenantId);
    Optional<Employee> findByTenantIdAndId(Long tenantId, Long id);
    boolean existsByTenantIdAndEmployeeCode(Long tenantId, String employeeCode);
}