package com.smartinventory.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartinventory.model.dto.MoveItemRequest;
import com.smartinventory.model.dto.MovementResponse;
import com.smartinventory.model.dto.StockInRequest;
import com.smartinventory.model.dto.StockOutRequest;
import com.smartinventory.security.TenantContext;
import com.smartinventory.service.MovementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService movementService;

    @PostMapping("/in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovementResponse> stockIn(@Valid @RequestBody StockInRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        MovementResponse response = movementService.stockIn(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/out")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovementResponse> stockOut(@Valid @RequestBody StockOutRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        MovementResponse response = movementService.stockOut(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/move")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovementResponse> moveItem(@Valid @RequestBody MoveItemRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        MovementResponse response = movementService.moveItem(request, tenantId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<MovementResponse>> getMovements(
            @RequestParam(required = false) UUID itemId,
            @PageableDefault(size = 20, sort = "timestamp") Pageable pageable) {
        String tenantId = TenantContext.getCurrentTenantId();
        return ResponseEntity.ok(movementService.getMovements(tenantId, itemId, pageable));
    }
}
