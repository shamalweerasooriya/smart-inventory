package com.smartinventory.model.dto;

import java.time.Instant;
import java.util.UUID;

import com.smartinventory.model.entity.Warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseResponse {

    private UUID id;
    private String name;
    private Integer width;
    private Integer height;
    private Integer entranceX;
    private Integer entranceY;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public static WarehouseResponse from(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .width(warehouse.getWidth())
                .height(warehouse.getHeight())
                .entranceX(warehouse.getEntranceX())
                .entranceY(warehouse.getEntranceY())
                .active(warehouse.isActive())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .build();
    }
}
