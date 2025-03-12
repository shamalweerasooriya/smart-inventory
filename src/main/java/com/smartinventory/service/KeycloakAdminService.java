package com.smartinventory.service;

import java.util.List;
import java.util.Map;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAdminService {

    private final Keycloak keycloak;

    @Value("${app.keycloak.realm}")
    private String realm;

    public String createUser(String email, String password, String firstName,
                             String lastName, String tenantId, String roleName) {
        UsersResource usersResource = getRealmResource().users();

        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setAttributes(Map.of("tenantId", List.of(tenantId)));

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        try (Response response = usersResource.create(user)) {
            if (response.getStatus() == 201) {
                String locationHeader = response.getHeaderString("Location");
                String userId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

                assignRealmRole(userId, roleName);
                log.info("Created Keycloak user {} with role {} for tenant {}", email, roleName, tenantId);
                return userId;
            } else if (response.getStatus() == 409) {
                throw new IllegalStateException("User with email " + email + " already exists");
            } else {
                String body = response.readEntity(String.class);
                throw new RuntimeException("Failed to create Keycloak user: " + response.getStatus() + " - " + body);
            }
        }
    }

    private void assignRealmRole(String userId, String roleName) {
        RoleRepresentation role = getRealmResource().roles().get(roleName).toRepresentation();
        getRealmResource().users().get(userId).roles().realmLevel().add(List.of(role));
    }

    private RealmResource getRealmResource() {
        return keycloak.realm(realm);
    }
}
