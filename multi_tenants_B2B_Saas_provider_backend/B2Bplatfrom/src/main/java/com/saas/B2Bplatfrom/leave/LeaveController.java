package com.saas.B2Bplatfrom.leave;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.saas.B2Bplatfrom.security.CurrentTenant;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveRepository repo;

    public LeaveController(LeaveRepository repo) {
        this.repo = repo;
    }

    // Apply for leave
    @PostMapping
    @PreAuthorize("hasAuthority('LEAVE_APPLY')")
    public ResponseEntity<?> apply(@RequestBody LeaveApplyRequest req, Authentication auth) {
        if (req.fromDate() == null || req.toDate() == null || req.toDate().isBefore(req.fromDate())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid dates"));
        }
        LeaveRequest l = new LeaveRequest();
        l.setTenantId(CurrentTenant.id());
        l.setApplicantEmail(auth.getName());
        l.setFromDate(req.fromDate());
        l.setToDate(req.toDate());
        l.setReason(req.reason());
        return ResponseEntity.ok(repo.save(l));
    }

    // My own leaves
    @GetMapping("/mine")
    @PreAuthorize("hasAuthority('LEAVE_APPLY')")
    public List<LeaveRequest> mine(Authentication auth) {
        return repo.findByTenantIdAndApplicantEmail(CurrentTenant.id(), auth.getName());
    }

    // All leaves in the company (approvers only)
    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    public List<LeaveRequest> all() {
        return repo.findByTenantId(CurrentTenant.id());
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    public ResponseEntity<?> approve(@PathVariable Long id, Authentication auth) {
        return decide(id, "APPROVED", auth.getName());
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    public ResponseEntity<?> reject(@PathVariable Long id, Authentication auth) {
        return decide(id, "REJECTED", auth.getName());
    }

    private ResponseEntity<?> decide(Long id, String status, String decidedBy) {
        return repo.findByTenantIdAndId(CurrentTenant.id(), id)
                .<ResponseEntity<?>>map(l -> {
                    if (!"PENDING".equals(l.getStatus())) {
                        return ResponseEntity.badRequest().body(Map.of("error", "Already " + l.getStatus()));
                    }
                    l.setStatus(status);
                    l.setDecidedBy(decidedBy);
                    return ResponseEntity.ok(repo.save(l));
                })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Not found")));
    }
}
