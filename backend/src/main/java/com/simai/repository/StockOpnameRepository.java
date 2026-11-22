package com.simai.repository;

import com.simai.domain.StockOpname;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockOpnameRepository extends JpaRepository<StockOpname, Long> {

    Optional<StockOpname> findByOpnameCode(String opnameCode);

    boolean existsByOpnameCode(String opnameCode);
}
