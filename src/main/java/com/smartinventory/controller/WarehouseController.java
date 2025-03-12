package com.smartinventory.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartinventory.model.dto.WarehouseRequest;
import com.smartinventory.model.dto.WarehouseResponse;
import com.smartinventory.security.TenantContext;
import com.smartinventory.service.WarehouseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> listWarehouses() {
        String tenantId = TenantContext.getCurrentTenantId();
        return ResponseEntity.ok(warehouseService.listWarehouses(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouse(@PathVariable UUID id) {
        String tenantId = TenantContext.getCurrentTenantId();
        return ResponseEntity.ok(warehouseService.getWarehouse(id, tenantId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WarehouseResponse> createWarehouse(
            @Valid @RequestBody WarehouseRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        WarehouseResponse response = warehouseService.createWarehouse(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WarehouseResponse> updateWarehouse(
            @PathVariable UUID id,
            @Valid @RequestBody WarehouseRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, request, tenantId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable UUID id) {
        String tenantId = TenantContext.getCurrentTenantId();
        warehouseService.deleteWarehouse(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}
