package com.saas.B2Bplatfrom.tenant;

public record RegisterTenantRequest(
        String companyName,
        String subdomain,
        String adminName,
        String adminEmail,
        String adminPassword) {}