package com.simai.repository;

import com.simai.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("""
            select i from Item i
            where (:q is null
                or lower(i.sku) like lower(concat('%', :q, '%'))
                or lower(i.name) like lower(concat('%', :q, '%')))
              and i.active = true
            """)
    Page<Item> search(@Param("q") String q, Pageable pageable);
}
