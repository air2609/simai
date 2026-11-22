package com.simai.repository;

import com.simai.domain.Asset;
import com.simai.domain.enums.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByAssetCode(String assetCode);

    boolean existsByAssetCode(String assetCode);

    Optional<Asset> findBySerialNumber(String serialNumber);

    Page<Asset> findByStatus(AssetStatus status, Pageable pageable);

    @Query("""
            select a from Asset a
            where (:q is null
                or lower(a.assetCode) like lower(concat('%', :q, '%'))
                or lower(a.name) like lower(concat('%', :q, '%'))
                or lower(a.serialNumber) like lower(concat('%', :q, '%')))
              and (:status is null or a.status = :status)
              and (:locationId is null or a.currentLocation.id = :locationId)
            """)
    Page<Asset> search(@Param("q") String q,
                       @Param("status") AssetStatus status,
                       @Param("locationId") Long locationId,
                       Pageable pageable);
}
