package com.simai.repository;

import com.simai.domain.StockOpnameItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockOpnameItemRepository extends JpaRepository<StockOpnameItem, Long> {

    List<StockOpnameItem> findByStockOpnameId(Long stockOpnameId);
}
