package com.smartinventory.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartinventory.model.entity.Movement;

@Repository
public interface MovementRepository extends JpaRepository<Movement, UUID> {

    Page<Movement> findByTenantId(String tenantId, Pageable pageable);

    Page<Movement> findByTenantIdAndItemId(String tenantId, UUID itemId, Pageable pageable);
}
