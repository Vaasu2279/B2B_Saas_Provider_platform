package com.saas.B2Bplatfrom.authentication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saas.B2Bplatfrom.security.JwtService;
import com.saas.B2Bplatfrom.tenant.Tenant;
import com.saas.B2Bplatfrom.tenant.TenantRepository;
import com.saas.B2Bplatfrom.user.Permission;
import com.saas.B2Bplatfrom.user.Role;
import com.saas.B2Bplatfrom.user.User;
import com.saas.B2Bplatfrom.user.UserRepository;

@Service
public class AuthService {

    private final TenantRepository tenantRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(TenantRepository tenantRepo, UserRepository userRepo,
                       PasswordEncoder encoder, JwtService jwtService) {
        this.tenantRepo = tenantRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest req) {
        Tenant tenant = tenantRepo.findBySubdomain(req.subdomain().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!"ACTIVE".equals(tenant.getStatus())) {
            throw new IllegalArgumentException("Company account is suspended");
        }

        User user = userRepo.findByTenantIdAndEmail(tenant.getId(),
                        req.email().trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!user.isActive() || !encoder.matches(req.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        List<String> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add("ROLE_" + role.getName());
            for (Permission p : role.getPermissions()) {
                if (!authorities.contains(p.getCode())) authorities.add(p.getCode());
            }
        }

        String token = jwtService.generate(user.getId(), tenant.getId(),
                user.getEmail(), authorities);
        return new LoginResponse(token, "Bearer", tenant.getId(), user.getEmail(), authorities);
    }
}