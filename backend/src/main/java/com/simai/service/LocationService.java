package com.simai.service;

import com.simai.common.exception.BusinessException;
import com.simai.common.exception.ResourceNotFoundException;
import com.simai.domain.Location;
import com.simai.domain.enums.AuditAction;
import com.simai.dto.master.LocationRequest;
import com.simai.dto.master.LocationResponse;
import com.simai.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<LocationResponse> findAll() {
        return locationRepository.findAll(Sort.by("name")).stream()
                .map(LocationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LocationResponse findById(Long id) {
        return LocationResponse.from(getEntity(id));
    }

    @Transactional
    public LocationResponse create(LocationRequest request) {
        ensureCodeAvailable(request.code(), null);

        Location location = new Location();
        location.setCode(request.code().trim());
        location.setName(request.name().trim());
        location.setParent(resolveParent(request.parentId(), null));
        location.setAddress(request.address());
        Location saved = locationRepository.save(location);

        auditService.log("LOCATION", String.valueOf(saved.getId()), AuditAction.CREATE, null, LocationResponse.from(saved));
        return LocationResponse.from(saved);
    }

    @Transactional
    public LocationResponse update(Long id, LocationRequest request) {
        Location location = getEntity(id);
        LocationResponse oldValue = LocationResponse.from(location);
        ensureCodeAvailable(request.code(), id);

        location.setCode(request.code().trim());
        location.setName(request.name().trim());
        location.setParent(resolveParent(request.parentId(), id));
        location.setAddress(request.address());
        Location saved = locationRepository.save(location);

        auditService.log("LOCATION", String.valueOf(id), AuditAction.UPDATE, oldValue, LocationResponse.from(saved));
        return LocationResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Location location = getEntity(id);
        locationRepository.delete(location);
        auditService.log("LOCATION", String.valueOf(id), AuditAction.DELETE, LocationResponse.from(location), null);
    }

    private Location getEntity(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lokasi", id));
    }

    private void ensureCodeAvailable(String code, Long excludeId) {
        locationRepository.findByCode(code.trim()).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BusinessException("Kode lokasi sudah digunakan");
            }
        });
    }

    private Location resolveParent(Long parentId, Long selfId) {
        if (parentId == null) {
            return null;
        }
        if (selfId != null && parentId.equals(selfId)) {
            throw new BusinessException("Lokasi tidak bisa menjadi parent dari dirinya sendiri");
        }
        return locationRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Lokasi parent", parentId));
    }
}
