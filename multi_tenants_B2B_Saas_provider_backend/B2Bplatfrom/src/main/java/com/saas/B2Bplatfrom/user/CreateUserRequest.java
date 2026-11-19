package com.saas.B2Bplatfrom.user;


public record CreateUserRequest(String fullName, String email, String password, String roleName) {}