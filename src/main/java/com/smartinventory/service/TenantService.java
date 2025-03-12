package com.smartinventory.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartinventory.model.dto.TenantRegistrationRequest;
import com.smartinventory.model.dto.TenantRegistrationResponse;
import com.smartinventory.model.entity.Tenant;
import com.smartinventory.repository.TenantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;
    private final KeycloakAdminService keycloakAdminService;

    @Transactional
    public TenantRegistrationResponse register(TenantRegistrationRequest request) {
        String tenantId = UUID.randomUUID().toString();

        Tenant tenant = Tenant.builder()
                .id(tenantId)
                .name(request.getOrganizationName())
                .build();
        tenantRepository.save(tenant);

        try {
            keycloakAdminService.createUser(
                    request.getAdminEmail(),
                    request.getAdminPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    tenantId,
                    "ADMIN"
            );
        } catch (Exception e) {
            log.error("Failed to create admin user in Keycloak for tenant {}: {}", tenantId, e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }

        log.info("Registered new tenant: {} ({})", request.getOrganizationName(), tenantId);

        return TenantRegistrationResponse.builder()
                .tenantId(tenantId)
                .organizationName(request.getOrganizationName())
                .adminEmail(request.getAdminEmail())
                .message("Tenant registered successfully")
                .build();
    }
}
