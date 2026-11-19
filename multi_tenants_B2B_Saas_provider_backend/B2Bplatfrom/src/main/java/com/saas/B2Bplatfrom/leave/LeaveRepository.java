package com.saas.B2Bplatfrom.leave;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByTenantId(Long tenantId);
    List<LeaveRequest> findByTenantIdAndApplicantEmail(Long tenantId, String email);
    Optional<LeaveRequest> findByTenantIdAndId(Long tenantId, Long id);
}