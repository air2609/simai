package com.simai.controller.master;

import com.simai.common.api.ApiResponse;
import com.simai.dto.master.VendorRequest;
import com.simai.dto.master.VendorResponse;
import com.simai.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @GetMapping
    public ApiResponse<List<VendorResponse>> findAll() {
        return ApiResponse.ok(vendorService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<VendorResponse> findById(@PathVariable Long id) {
        return ApiResponse.ok(vendorService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<VendorResponse> create(@Valid @RequestBody VendorRequest request) {
        return ApiResponse.ok("Vendor berhasil dibuat", vendorService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<VendorResponse> update(@PathVariable Long id,
                                              @Valid @RequestBody VendorRequest request) {
        return ApiResponse.ok("Vendor berhasil diperbarui", vendorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        vendorService.delete(id);
        return ApiResponse.ok("Vendor berhasil dihapus", null);
    }
}
