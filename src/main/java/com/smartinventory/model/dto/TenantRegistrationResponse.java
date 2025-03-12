package com.smartinventory.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantRegistrationResponse {

    private String tenantId;
    private String organizationName;
    private String adminEmail;
    private String message;
}
