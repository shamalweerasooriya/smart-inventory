package com.smartinventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartinventory.model.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    List<Location> findByWarehouseIdAndTenantId(UUID warehouseId, String tenantId);

    Optional<Location> findByWarehouseIdAndXAndY(UUID warehouseId, int x, int y);

    void deleteByWarehouseId(UUID warehouseId);
}
