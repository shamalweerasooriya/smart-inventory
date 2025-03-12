package com.smartinventory.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartinventory.exception.ResourceNotFoundException;
import com.smartinventory.model.dto.WarehouseRequest;
import com.smartinventory.model.dto.WarehouseResponse;
import com.smartinventory.model.entity.Location;
import com.smartinventory.model.entity.Warehouse;
import com.smartinventory.model.enums.LocationType;
import com.smartinventory.repository.LocationRepository;
import com.smartinventory.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public List<WarehouseResponse> listWarehouses(String tenantId) {
        return warehouseRepository.findByTenantIdAndActiveTrue(tenantId).stream()
                .map(WarehouseResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouse(UUID id, String tenantId) {
        Warehouse warehouse = findWarehouseOrThrow(id, tenantId);
        return WarehouseResponse.from(warehouse);
    }

    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest request, String tenantId) {
        validateEntranceOnEdge(request);

        Warehouse warehouse = Warehouse.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .width(request.getWidth())
                .height(request.getHeight())
                .entranceX(request.getEntranceX())
                .entranceY(request.getEntranceY())
                .build();

        warehouse = warehouseRepository.save(warehouse);

        Location entrance = Location.builder()
                .tenantId(tenantId)
                .warehouse(warehouse)
                .x(request.getEntranceX())
                .y(request.getEntranceY())
                .type(LocationType.ENTRANCE)
                .build();
        locationRepository.save(entrance);

        return WarehouseResponse.from(warehouse);
    }

    @Transactional
    public WarehouseResponse updateWarehouse(UUID id, WarehouseRequest request, String tenantId) {
        Warehouse warehouse = findWarehouseOrThrow(id, tenantId);
        warehouse.setName(request.getName());
        warehouse = warehouseRepository.save(warehouse);
        return WarehouseResponse.from(warehouse);
    }

    @Transactional
    public void deleteWarehouse(UUID id, String tenantId) {
        Warehouse warehouse = findWarehouseOrThrow(id, tenantId);
        warehouse.setActive(false);
        warehouseRepository.save(warehouse);
    }

    private Warehouse findWarehouseOrThrow(UUID id, String tenantId) {
        return warehouseRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", id));
    }

    private void validateEntranceOnEdge(WarehouseRequest request) {
        int x = request.getEntranceX();
        int y = request.getEntranceY();
        int w = request.getWidth();
        int h = request.getHeight();

        if (x < 0 || x >= w || y < 0 || y >= h) {
            throw new IllegalArgumentException("Entrance coordinates must be within the grid");
        }

        boolean onEdge = (x == 0 || x == w - 1 || y == 0 || y == h - 1);
        if (!onEdge) {
            throw new IllegalArgumentException("Entrance must be on the edge of the warehouse grid");
        }
    }
}
