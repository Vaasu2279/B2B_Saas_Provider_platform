package com.saas.B2Bplatfrom.common;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.saas.B2Bplatfrom.user.Permission;
import com.saas.B2Bplatfrom.user.PermissionRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final List<String> CODES = List.of(
            "EMPLOYEE_READ", "EMPLOYEE_WRITE", "EMPLOYEE_DELETE",
            "DEPARTMENT_MANAGE", "LEAVE_APPLY", "LEAVE_APPROVE",
            "USER_MANAGE", "ROLE_MANAGE");

    private final PermissionRepository repo;

    public DataInitializer(PermissionRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        for (String code : CODES) {
            if (!repo.existsByCode(code)) {
                repo.save(new Permission(code));
            }
        }
    }
}