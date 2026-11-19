package com.saas.B2Bplatfrom.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByTenantId(Long tenantId);
    Optional<Role> findByTenantIdAndName(Long tenantId, String name);
}
