package com.smartinventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartinventory.model.entity.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    List<Warehouse> findByTenantIdAndActiveTrue(String tenantId);

    Optional<Warehouse> findByIdAndTenantId(UUID id, String tenantId);
}
