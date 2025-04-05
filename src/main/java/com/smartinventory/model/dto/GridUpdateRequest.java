package com.smartinventory.model.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GridUpdateRequest {

    @NotNull(message = "Cells list is required")
    @Valid
    private List<GridCellRequest> cells;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GridCellRequest {

        @NotNull(message = "X coordinate is required")
        private Integer x;

        @NotNull(message = "Y coordinate is required")
        private Integer y;

        @NotNull(message = "Cell type is required")
        private String type;
    }
}
