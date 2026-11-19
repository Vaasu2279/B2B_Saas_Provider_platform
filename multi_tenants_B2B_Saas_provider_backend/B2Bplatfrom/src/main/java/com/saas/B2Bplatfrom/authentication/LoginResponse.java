package com.saas.B2Bplatfrom.authentication;

import java.util.List;

public record LoginResponse(String accessToken, String tokenType,
                            Long tenantId, String email, List<String> authorities) {}
