package com.smartinventory.model.dto;

import java.time.Instant;
import java.util.UUID;

import com.smartinventory.model.entity.Movement;
import com.smartinventory.model.enums.MovementType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementResponse {

    private UUID id;
    private UUID itemId;
    private String itemName;
    private String itemSku;
    private MovementType type;
    private UUID fromLocationId;
    private UUID toLocationId;
    private Integer quantity;
    private String userId;
    private Instant timestamp;

    public static MovementResponse from(Movement movement) {
        MovementResponseBuilder builder = MovementResponse.builder()
                .id(movement.getId())
                .itemId(movement.getItem().getId())
                .itemName(movement.getItem().getName())
                .itemSku(movement.getItem().getSku())
                .type(movement.getType())
                .quantity(movement.getQuantity())
                .userId(movement.getUserId())
                .timestamp(movement.getTimestamp());

        if (movement.getFromLocation() != null) {
            builder.fromLocationId(movement.getFromLocation().getId());
        }
        if (movement.getToLocation() != null) {
            builder.toLocationId(movement.getToLocation().getId());
        }

        return builder.build();
    }
}
