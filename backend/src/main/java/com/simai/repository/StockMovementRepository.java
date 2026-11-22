package com.simai.repository;

import com.simai.domain.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    Page<StockMovement> findByItemIdOrderByCreatedAtDesc(Long itemId, Pageable pageable);

    Page<StockMovement> findByLocationIdOrderByCreatedAtDesc(Long locationId, Pageable pageable);
}
