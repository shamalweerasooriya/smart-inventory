package com.smartinventory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartinventory.exception.ResourceNotFoundException;
import com.smartinventory.model.dto.GridCellResponse;
import com.smartinventory.model.dto.GridResponse;
import com.smartinventory.model.dto.GridUpdateRequest;
import com.smartinventory.model.entity.Location;
import com.smartinventory.model.entity.Warehouse;
import com.smartinventory.model.enums.LocationType;
import com.smartinventory.repository.LocationRepository;
import com.smartinventory.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GridService {

    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public GridResponse getGrid(UUID warehouseId, String tenantId) {
        Warehouse warehouse = findWarehouseOrThrow(warehouseId, tenantId);

        List<Location> locations = locationRepository.findByWarehouseIdAndTenantId(warehouseId, tenantId);
        List<GridCellResponse> cells = locations.stream()
                .map(GridCellResponse::from)
                .toList();

        return GridResponse.builder()
                .warehouseId(warehouse.getId())
                .warehouseName(warehouse.getName())
                .width(warehouse.getWidth())
                .height(warehouse.getHeight())
                .entranceX(warehouse.getEntranceX())
                .entranceY(warehouse.getEntranceY())
                .cells(cells)
                .build();
    }

    @Transactional
    public GridResponse updateGrid(UUID warehouseId, GridUpdateRequest request, String tenantId) {
        Warehouse warehouse = findWarehouseOrThrow(warehouseId, tenantId);

        List<Location> toSave = new ArrayList<>();
        List<Location> toRemove = new ArrayList<>();

        for (GridUpdateRequest.GridCellRequest cell : request.getCells()) {
            validateCoordinates(cell.getX(), cell.getY(), warehouse);
            LocationType type = parseLocationType(cell.getType());

            if (isEntranceCell(cell.getX(), cell.getY(), warehouse)) {
                throw new IllegalArgumentException(
                        "Cannot modify the entrance cell at (" + cell.getX() + "," + cell.getY() + ")");
            }

            Location existing = locationRepository
                    .findByWarehouseIdAndXAndY(warehouseId, cell.getX(), cell.getY())
                    .orElse(null);

            if (type == LocationType.EMPTY) {
                if (existing != null) {
                    toRemove.add(existing);
                }
            } else {
                if (existing != null) {
                    existing.setType(type);
                    toSave.add(existing);
                } else {
                    toSave.add(Location.builder()
                            .tenantId(tenantId)
                            .warehouse(warehouse)
                            .x(cell.getX())
                            .y(cell.getY())
                            .type(type)
                            .build());
                }
            }
        }

        if (!toRemove.isEmpty()) {
            locationRepository.deleteAll(toRemove);
        }
        if (!toSave.isEmpty()) {
            locationRepository.saveAll(toSave);
        }

        return getGrid(warehouseId, tenantId);
    }

    private Warehouse findWarehouseOrThrow(UUID warehouseId, String tenantId) {
        return warehouseRepository.findByIdAndTenantId(warehouseId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));
    }

    private void validateCoordinates(int x, int y, Warehouse warehouse) {
        if (x < 0 || x >= warehouse.getWidth() || y < 0 || y >= warehouse.getHeight()) {
            throw new IllegalArgumentException(
                    "Coordinates (" + x + "," + y + ") are outside the grid bounds");
        }
    }

    private boolean isEntranceCell(int x, int y, Warehouse warehouse) {
        return x == warehouse.getEntranceX() && y == warehouse.getEntranceY();
    }

    private LocationType parseLocationType(String type) {
        try {
            return LocationType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid location type: " + type
                    + ". Valid types: EMPTY, RACK, OBSTACLE");
        }
    }
}
