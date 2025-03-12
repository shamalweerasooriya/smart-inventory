package com.smartinventory.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseRequest {

    @NotBlank(message = "Warehouse name is required")
    private String name;

    @NotNull(message = "Width is required")
    @Min(value = 2, message = "Width must be at least 2")
    @Max(value = 50, message = "Width must be at most 50")
    private Integer width;

    @NotNull(message = "Height is required")
    @Min(value = 2, message = "Height must be at least 2")
    @Max(value = 50, message = "Height must be at most 50")
    private Integer height;

    @NotNull(message = "Entrance X coordinate is required")
    @Min(0)
    private Integer entranceX;

    @NotNull(message = "Entrance Y coordinate is required")
    @Min(0)
    private Integer entranceY;
}
