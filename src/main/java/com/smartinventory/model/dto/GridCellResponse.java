package com.smartinventory.model.dto;

import java.util.UUID;

import com.smartinventory.model.entity.Location;
import com.smartinventory.model.enums.LocationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GridCellResponse {

    private UUID locationId;
    private int x;
    private int y;
    private LocationType type;

    public static GridCellResponse from(Location location) {
        return GridCellResponse.builder()
                .locationId(location.getId())
                .x(location.getX())
                .y(location.getY())
                .type(location.getType())
                .build();
    }
}
