package com.simai.repository;

import com.simai.domain.AssetMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetMaintenanceRepository extends JpaRepository<AssetMaintenance, Long> {

    List<AssetMaintenance> findByAssetIdOrderByMaintenanceDateDesc(Long assetId);
}
