package com.smartinventory.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartinventory.exception.DuplicateResourceException;
import com.smartinventory.exception.ResourceNotFoundException;
import com.smartinventory.model.dto.ItemRequest;
import com.smartinventory.model.dto.ItemResponse;
import com.smartinventory.model.entity.Item;
import com.smartinventory.model.entity.Location;
import com.smartinventory.model.entity.Warehouse;
import com.smartinventory.repository.ItemRepository;
import com.smartinventory.repository.LocationRepository;
import com.smartinventory.repository.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final WarehouseRepository warehouseRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public Page<ItemResponse> listItems(String tenantId, UUID warehouseId, Pageable pageable) {
        if (warehouseId != null) {
            return itemRepository.findByTenantIdAndWarehouseIdAndActiveTrue(tenantId, warehouseId, pageable)
                    .map(ItemResponse::from);
        }
        return itemRepository.findByTenantIdAndActiveTrue(tenantId, pageable)
                .map(ItemResponse::from);
    }

    @Transactional(readOnly = true)
    public ItemResponse getItem(UUID id, String tenantId) {
        Item item = findItemOrThrow(id, tenantId);
        return ItemResponse.from(item);
    }

    @Transactional
    public ItemResponse createItem(ItemRequest request, String tenantId) {
        if (itemRepository.existsByTenantIdAndSku(tenantId, request.getSku())) {
            throw new DuplicateResourceException("Item with SKU '" + request.getSku() + "' already exists");
        }

        Warehouse warehouse = warehouseRepository.findByIdAndTenantId(request.getWarehouseId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", request.getWarehouseId()));

        Item.ItemBuilder builder = Item.builder()
                .tenantId(tenantId)
                .warehouse(warehouse)
                .name(request.getName())
                .sku(request.getSku())
                .quantity(request.getQuantity());

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .filter(loc -> loc.getTenantId().equals(tenantId))
                    .orElseThrow(() -> new ResourceNotFoundException("Location", request.getLocationId()));
            builder.location(location);
        }

        Item item = itemRepository.save(builder.build());
        return ItemResponse.from(item);
    }

    @Transactional
    public ItemResponse updateItem(UUID id, ItemRequest request, String tenantId) {
        Item item = findItemOrThrow(id, tenantId);

        item.setName(request.getName());
        item.setQuantity(request.getQuantity());

        if (request.getLocationId() != null) {
            Location location = locationRepository.findById(request.getLocationId())
                    .filter(loc -> loc.getTenantId().equals(tenantId))
                    .orElseThrow(() -> new ResourceNotFoundException("Location", request.getLocationId()));
            item.setLocation(location);
        } else {
            item.setLocation(null);
        }

        item = itemRepository.save(item);
        return ItemResponse.from(item);
    }

    @Transactional
    public void deleteItem(UUID id, String tenantId) {
        Item item = findItemOrThrow(id, tenantId);
        item.setActive(false);
        itemRepository.save(item);
    }

    private Item findItemOrThrow(UUID id, String tenantId) {
        return itemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }
}
