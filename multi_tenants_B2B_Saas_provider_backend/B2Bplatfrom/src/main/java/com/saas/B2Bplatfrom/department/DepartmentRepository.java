package com.saas.B2Bplatfrom.department;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByTenantId(Long tenantId);
    Optional<Department> findByTenantIdAndId(Long tenantId, Long id);
    boolean existsByTenantIdAndName(Long tenantId, String name);
}