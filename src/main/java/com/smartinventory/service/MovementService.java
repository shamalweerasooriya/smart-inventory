package com.smartinventory.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartinventory.exception.ResourceNotFoundException;
import com.smartinventory.model.dto.MoveItemRequest;
import com.smartinventory.model.dto.MovementResponse;
import com.smartinventory.model.dto.StockInRequest;
import com.smartinventory.model.dto.StockOutRequest;
import com.smartinventory.model.entity.Item;
import com.smartinventory.model.entity.Location;
import com.smartinventory.model.entity.Movement;
import com.smartinventory.model.enums.LocationType;
import com.smartinventory.model.enums.MovementType;
import com.smartinventory.repository.ItemRepository;
import com.smartinventory.repository.LocationRepository;
import com.smartinventory.repository.MovementRepository;
import com.smartinventory.security.TenantContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;
    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public MovementResponse stockIn(StockInRequest request, String tenantId) {
        Item item = findItemOrThrow(request.getItemId(), tenantId);
        Location toLocation = findLocationOrThrow(request.getToLocationId(), tenantId);

        validateLocationIsRack(toLocation);

        item.setQuantity(item.getQuantity() + request.getQuantity());
        item.setLocation(toLocation);
        itemRepository.save(item);

        Movement movement = Movement.builder()
                .tenantId(tenantId)
                .item(item)
                .type(MovementType.IN)
                .toLocation(toLocation)
                .quantity(request.getQuantity())
                .userId(TenantContext.getCurrentUserId())
                .build();

        movement = movementRepository.save(movement);
        return MovementResponse.from(movement);
    }

    @Transactional
    public MovementResponse stockOut(StockOutRequest request, String tenantId) {
        Item item = findItemOrThrow(request.getItemId(), tenantId);

        if (item.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Insufficient stock. Available: " + item.getQuantity()
                            + ", Requested: " + request.getQuantity());
        }

        Location fromLocation = item.getLocation();
        item.setQuantity(item.getQuantity() - request.getQuantity());

        if (item.getQuantity() == 0) {
            item.setLocation(null);
        }

        itemRepository.save(item);

        Movement movement = Movement.builder()
                .tenantId(tenantId)
                .item(item)
                .type(MovementType.OUT)
                .fromLocation(fromLocation)
                .quantity(request.getQuantity())
                .userId(TenantContext.getCurrentUserId())
                .build();

        movement = movementRepository.save(movement);
        return MovementResponse.from(movement);
    }

    @Transactional
    public MovementResponse moveItem(MoveItemRequest request, String tenantId) {
        Item item = findItemOrThrow(request.getItemId(), tenantId);
        Location toLocation = findLocationOrThrow(request.getToLocationId(), tenantId);

        validateLocationIsRack(toLocation);

        if (item.getLocation() == null) {
            throw new IllegalStateException("Item is not currently assigned to a location");
        }

        Location fromLocation = item.getLocation();

        if (fromLocation.getId().equals(toLocation.getId())) {
            throw new IllegalArgumentException("Source and destination locations are the same");
        }

        item.setLocation(toLocation);
        itemRepository.save(item);

        Movement movement = Movement.builder()
                .tenantId(tenantId)
                .item(item)
                .type(MovementType.MOVE)
                .fromLocation(fromLocation)
                .toLocation(toLocation)
                .quantity(item.getQuantity())
                .userId(TenantContext.getCurrentUserId())
                .build();

        movement = movementRepository.save(movement);
        return MovementResponse.from(movement);
    }

    @Transactional(readOnly = true)
    public Page<MovementResponse> getMovements(String tenantId, UUID itemId, Pageable pageable) {
        if (itemId != null) {
            return movementRepository.findByTenantIdAndItemId(tenantId, itemId, pageable)
                    .map(MovementResponse::from);
        }
        return movementRepository.findByTenantId(tenantId, pageable)
                .map(MovementResponse::from);
    }

    private Item findItemOrThrow(UUID itemId, String tenantId) {
        return itemRepository.findByIdAndTenantId(itemId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", itemId));
    }

    private Location findLocationOrThrow(UUID locationId, String tenantId) {
        return locationRepository.findById(locationId)
                .filter(loc -> loc.getTenantId().equals(tenantId))
                .orElseThrow(() -> new ResourceNotFoundException("Location", locationId));
    }

    private void validateLocationIsRack(Location location) {
        if (location.getType() != LocationType.RACK) {
            throw new IllegalArgumentException(
                    "Items can only be placed in RACK locations. Location type: " + location.getType());
        }
    }
}
