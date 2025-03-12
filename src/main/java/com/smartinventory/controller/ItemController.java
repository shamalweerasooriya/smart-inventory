package com.smartinventory.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartinventory.model.dto.ItemRequest;
import com.smartinventory.model.dto.ItemResponse;
import com.smartinventory.security.TenantContext;
import com.smartinventory.service.ItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> listItems(
            @RequestParam(required = false) UUID warehouseId,
            @PageableDefault(size = 20) Pageable pageable) {
        String tenantId = TenantContext.getCurrentTenantId();
        return ResponseEntity.ok(itemService.listItems(tenantId, warehouseId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable UUID id) {
        String tenantId = TenantContext.getCurrentTenantId();
        return ResponseEntity.ok(itemService.getItem(id, tenantId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody ItemRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        ItemResponse response = itemService.createItem(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody ItemRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        return ResponseEntity.ok(itemService.updateItem(id, request, tenantId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        String tenantId = TenantContext.getCurrentTenantId();
        itemService.deleteItem(id, tenantId);
        return ResponseEntity.noContent().build();
    }
}
