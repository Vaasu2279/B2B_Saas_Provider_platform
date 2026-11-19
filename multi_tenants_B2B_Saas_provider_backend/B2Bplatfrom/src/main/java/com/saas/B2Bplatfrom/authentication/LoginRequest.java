package com.saas.B2Bplatfrom.authentication;

public record LoginRequest(String subdomain, String email, String password) {}