package com.saas.B2Bplatfrom.user;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.saas.B2Bplatfrom.security.CurrentTenant;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    public UserController(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.encoder = encoder;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Transactional(readOnly = true)
    public List<Map<String, Object>> list() {
        return userRepo.findByTenantId(CurrentTenant.id()).stream()
                .map(u -> Map.<String, Object>of(
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "fullName", String.valueOf(u.getFullName()),
                        "active", u.isActive(),
                        "roles", u.getRoles().stream().map(Role::getName).toList()))
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    @Transactional
    public ResponseEntity<?> create(@RequestBody CreateUserRequest req) {
        Long tenantId = CurrentTenant.id();
        String email = req.email().trim().toLowerCase();

        if (userRepo.existsByTenantIdAndEmail(tenantId, email)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }
        Role role = roleRepo.findByTenantIdAndName(tenantId, req.roleName()).orElse(null);
        if (role == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Role not found"));
        }

        User u = new User();
        u.setTenantId(tenantId);
        u.setEmail(email);
        u.setFullName(req.fullName());
        u.setPasswordHash(encoder.encode(req.password()));
        u.getRoles().add(role);
        userRepo.save(u);

        return ResponseEntity.ok(Map.of("id", u.getId(), "email", u.getEmail(), "role", role.getName()));
    }
}
