package com.smartinventory.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", Instant.now().toString());
        response.put("service", "smart-inventory-backend");

        if (jwt != null) {
            response.put("authenticated", true);
            response.put("tenantId", jwt.getClaimAsString("tenantId"));
            response.put("userId", jwt.getSubject());
            response.put("email", jwt.getClaimAsString("email"));
        } else {
            response.put("authenticated", false);
        }

        return ResponseEntity.ok(response);
    }
}
