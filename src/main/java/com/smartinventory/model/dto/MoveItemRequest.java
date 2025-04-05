package com.smartinventory.model.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveItemRequest {

    @NotNull(message = "Item ID is required")
    private UUID itemId;

    @NotNull(message = "Target location ID is required")
    private UUID toLocationId;
}
