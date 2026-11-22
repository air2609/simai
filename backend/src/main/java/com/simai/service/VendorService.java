package com.simai.service;

import com.simai.common.exception.BusinessException;
import com.simai.common.exception.ResourceNotFoundException;
import com.simai.domain.Vendor;
import com.simai.domain.enums.AuditAction;
import com.simai.dto.master.VendorRequest;
import com.simai.dto.master.VendorResponse;
import com.simai.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<VendorResponse> findAll() {
        return vendorRepository.findAll(Sort.by("name")).stream()
                .map(VendorResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public VendorResponse findById(Long id) {
        return VendorResponse.from(getEntity(id));
    }

    @Transactional
    public VendorResponse create(VendorRequest request) {
        ensureCodeAvailable(request.code(), null);

        Vendor vendor = new Vendor();
        vendor.setCode(request.code().trim());
        vendor.setName(request.name().trim());
        vendor.setPhone(request.phone());
        vendor.setEmail(request.email());
        vendor.setAddress(request.address());
        Vendor saved = vendorRepository.save(vendor);

        auditService.log("VENDOR", String.valueOf(saved.getId()), AuditAction.CREATE, null, VendorResponse.from(saved));
        return VendorResponse.from(saved);
    }

    @Transactional
    public VendorResponse update(Long id, VendorRequest request) {
        Vendor vendor = getEntity(id);
        VendorResponse oldValue = VendorResponse.from(vendor);
        ensureCodeAvailable(request.code(), id);

        vendor.setCode(request.code().trim());
        vendor.setName(request.name().trim());
        vendor.setPhone(request.phone());
        vendor.setEmail(request.email());
        vendor.setAddress(request.address());
        Vendor saved = vendorRepository.save(vendor);

        auditService.log("VENDOR", String.valueOf(id), AuditAction.UPDATE, oldValue, VendorResponse.from(saved));
        return VendorResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Vendor vendor = getEntity(id);
        vendorRepository.delete(vendor);
        auditService.log("VENDOR", String.valueOf(id), AuditAction.DELETE, VendorResponse.from(vendor), null);
    }

    private Vendor getEntity(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", id));
    }

    private void ensureCodeAvailable(String code, Long excludeId) {
        vendorRepository.findByCode(code.trim()).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BusinessException("Kode vendor sudah digunakan");
            }
        });
    }
}
