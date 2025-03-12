package com.smartinventory.model.dto;

import java.time.Instant;
import java.util.UUID;

import com.smartinventory.model.entity.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {

    private UUID id;
    private String name;
    private String sku;
    private UUID warehouseId;
    private String warehouseName;
    private UUID locationId;
    private Integer locationX;
    private Integer locationY;
    private Integer quantity;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public static ItemResponse from(Item item) {
        ItemResponseBuilder builder = ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .sku(item.getSku())
                .warehouseId(item.getWarehouse().getId())
                .warehouseName(item.getWarehouse().getName())
                .quantity(item.getQuantity())
                .active(item.isActive())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt());

        if (item.getLocation() != null) {
            builder.locationId(item.getLocation().getId())
                   .locationX(item.getLocation().getX())
                   .locationY(item.getLocation().getY());
        }

        return builder.build();
    }
}
