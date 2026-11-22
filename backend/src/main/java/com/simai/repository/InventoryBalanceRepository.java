package com.simai.repository;

import com.simai.domain.InventoryBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryBalanceRepository extends JpaRepository<InventoryBalance, Long> {

    Optional<InventoryBalance> findByItemIdAndLocationId(Long itemId, Long locationId);

    List<InventoryBalance> findByItemId(Long itemId);
}
