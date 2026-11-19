package com.saas.B2Bplatfrom.department;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.saas.B2Bplatfrom.security.CurrentTenant;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentRepository repo;

    public DepartmentController(DepartmentRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_READ') or hasAuthority('DEPARTMENT_MANAGE')")
    public List<Department> list() {
        return repo.findByTenantId(CurrentTenant.id());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('DEPARTMENT_MANAGE')")
    public ResponseEntity<?> create(@RequestBody DepartmentRequest req) {
        Long tenantId = CurrentTenant.id();
        if (req.name() == null || req.name().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));
        }
        if (repo.existsByTenantIdAndName(tenantId, req.name().trim())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Department already exists"));
        }
        Department d = new Department();
        d.setTenantId(tenantId);
        d.setName(req.name().trim());
        return ResponseEntity.ok(repo.save(d));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_MANAGE')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repo.findByTenantIdAndId(CurrentTenant.id(), id)
                .map(d -> { repo.delete(d); return ResponseEntity.ok(Map.of("message", "Deleted")); })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Not found")));
    }
}