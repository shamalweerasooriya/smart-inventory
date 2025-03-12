package com.smartinventory.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.smartinventory.model.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID>, JpaSpecificationExecutor<Item> {

    Page<Item> findByTenantIdAndActiveTrue(String tenantId, Pageable pageable);

    Optional<Item> findByIdAndTenantId(UUID id, String tenantId);

    boolean existsByTenantIdAndSku(String tenantId, String sku);

    Page<Item> findByTenantIdAndWarehouseIdAndActiveTrue(String tenantId, UUID warehouseId, Pageable pageable);
}
