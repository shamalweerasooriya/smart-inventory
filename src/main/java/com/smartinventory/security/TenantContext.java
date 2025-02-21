package com.smartinventory.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class TenantContext {

    public static String getCurrentTenantId() {
        Jwt jwt = getJwt();
        String tenantId = jwt.getClaimAsString("tenantId");
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalStateException("No tenantId found in JWT claims");
        }
        return tenantId;
    }

    public static String getCurrentUserId() {
        return getJwt().getSubject();
    }

    public static String getCurrentUserEmail() {
        return getJwt().getClaimAsString("email");
    }

    private static Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        throw new IllegalStateException("No authenticated user in security context");
    }
}
