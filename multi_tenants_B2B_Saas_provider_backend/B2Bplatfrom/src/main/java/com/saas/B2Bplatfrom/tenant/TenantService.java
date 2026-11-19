package com.saas.B2Bplatfrom.tenant;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saas.B2Bplatfrom.user.*;

@Service
public class TenantService {

    private final TenantRepository tenantRepo;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PermissionRepository permissionRepo;
    private final PasswordEncoder encoder;

    public TenantService(TenantRepository tenantRepo, RoleRepository roleRepo,
                         UserRepository userRepo, PermissionRepository permissionRepo,
                         PasswordEncoder encoder) {
        this.tenantRepo = tenantRepo;
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.permissionRepo = permissionRepo;
        this.encoder = encoder;
    }

    @Transactional
    public Tenant register(RegisterTenantRequest req) {
        String sub = req.subdomain().trim().toLowerCase();
        if (tenantRepo.existsBySubdomain(sub)) {
            throw new IllegalArgumentException("Subdomain already taken");
        }

        Tenant tenant = new Tenant();
        tenant.setName(req.companyName());
        tenant.setSubdomain(sub);
        tenant = tenantRepo.save(tenant);
        Long tenantId = tenant.getId();

        List<Permission> all = permissionRepo.findAll();

        Role admin = newRole(tenantId, "COMPANY_ADMIN", new HashSet<>(all));
        newRole(tenantId, "HR_MANAGER", pick(all,
                "EMPLOYEE_READ", "EMPLOYEE_WRITE", "EMPLOYEE_DELETE",
                "DEPARTMENT_MANAGE", "LEAVE_APPROVE"));
        newRole(tenantId, "MANAGER", pick(all, "EMPLOYEE_READ", "LEAVE_APPROVE"));
        newRole(tenantId, "EMPLOYEE", pick(all, "LEAVE_APPLY"));

        User user = new User();
        user.setTenantId(tenantId);
        user.setEmail(req.adminEmail().trim().toLowerCase());
        user.setFullName(req.adminName());
        user.setPasswordHash(encoder.encode(req.adminPassword()));
        user.getRoles().add(admin);
        userRepo.save(user);

        return tenant;
    }

    private Role newRole(Long tenantId, String name, Set<Permission> perms) {
        Role r = new Role();
        r.setTenantId(tenantId);
        r.setName(name);
        r.setPermissions(perms);
        return roleRepo.save(r);
    }

    private Set<Permission> pick(List<Permission> all, String... codes) {
        List<String> wanted = List.of(codes);
        return all.stream().filter(p -> wanted.contains(p.getCode()))
                  .collect(Collectors.toSet());
    }
}
