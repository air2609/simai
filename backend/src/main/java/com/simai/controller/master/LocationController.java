package com.simai.controller.master;

import com.simai.common.api.ApiResponse;
import com.simai.dto.master.LocationRequest;
import com.simai.dto.master.LocationResponse;
import com.simai.service.LocationService;
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
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ApiResponse<List<LocationResponse>> findAll() {
        return ApiResponse.ok(locationService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<LocationResponse> findById(@PathVariable Long id) {
        return ApiResponse.ok(locationService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<LocationResponse> create(@Valid @RequestBody LocationRequest request) {
        return ApiResponse.ok("Lokasi berhasil dibuat", locationService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<LocationResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody LocationRequest request) {
        return ApiResponse.ok("Lokasi berhasil diperbarui", locationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ApiResponse.ok("Lokasi berhasil dihapus", null);
    }
}
