package com.saas.B2Bplatfrom.user;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saas.B2Bplatfrom.security.CurrentTenant;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleRepository roleRepo;

    public RoleController(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public List<String> list() {
        return roleRepo.findByTenantId(CurrentTenant.id()).stream()
                .map(Role::getName).toList();
    }
}