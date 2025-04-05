package com.smartinventory.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartinventory.model.dto.GridResponse;
import com.smartinventory.model.dto.GridUpdateRequest;
import com.smartinventory.security.TenantContext;
import com.smartinventory.service.GridService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/warehouses/{warehouseId}/grid")
@RequiredArgsConstructor
public class GridController {

    private final GridService gridService;

    @GetMapping
    public ResponseEntity<GridResponse> getGrid(@PathVariable UUID warehouseId) {
        String tenantId = TenantContext.getCurrentTenantId();
        return ResponseEntity.ok(gridService.getGrid(warehouseId, tenantId));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GridResponse> updateGrid(
            @PathVariable UUID warehouseId,
            @Valid @RequestBody GridUpdateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        return ResponseEntity.ok(gridService.updateGrid(warehouseId, request, tenantId));
    }
}
