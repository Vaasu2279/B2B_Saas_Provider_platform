package com.saas.B2Bplatfrom.employee;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.saas.B2Bplatfrom.security.CurrentTenant;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository repo;

    public EmployeeController(EmployeeRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_READ')")
    public List<Employee> list() {
        return repo.findByTenantId(CurrentTenant.id());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_READ')")
    public ResponseEntity<?> get(@PathVariable Long id) {
        return repo.findByTenantIdAndId(CurrentTenant.id(), id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Not found")));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_WRITE')")
    public ResponseEntity<?> create(@RequestBody EmployeeRequest req) {
        Long tenantId = CurrentTenant.id();
        if (req.employeeCode() == null || req.fullName() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "employeeCode and fullName are required"));
        }
        if (repo.existsByTenantIdAndEmployeeCode(tenantId, req.employeeCode())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Employee code already exists"));
        }
        Employee e = new Employee();
        e.setTenantId(tenantId);
        apply(e, req);
        return ResponseEntity.ok(repo.save(e));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_WRITE')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EmployeeRequest req) {
        return repo.findByTenantIdAndId(CurrentTenant.id(), id)
                .<ResponseEntity<?>>map(e -> { apply(e, req); return ResponseEntity.ok(repo.save(e)); })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Not found")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_DELETE')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return repo.findByTenantIdAndId(CurrentTenant.id(), id)
                .map(e -> { repo.delete(e); return ResponseEntity.ok(Map.of("message", "Deleted")); })
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Not found")));
    }

    private void apply(Employee e, EmployeeRequest req) {
        e.setDepartmentId(req.departmentId());
        e.setEmployeeCode(req.employeeCode());
        e.setFullName(req.fullName());
        e.setEmail(req.email());
        e.setJobTitle(req.jobTitle());
        e.setSalary(req.salary());
        e.setJoinDate(req.joinDate());
    }
}
