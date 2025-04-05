package com.smartinventory.model.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GridResponse {

    private UUID warehouseId;
    private String warehouseName;
    private int width;
    private int height;
    private int entranceX;
    private int entranceY;
    private List<GridCellResponse> cells;
}
