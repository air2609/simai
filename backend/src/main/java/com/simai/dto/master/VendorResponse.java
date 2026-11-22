package com.simai.dto.master;

import com.simai.domain.Vendor;

public record VendorResponse(
        Long id,
        String code,
        String name,
        String phone,
        String email,
        String address
) {

    public static VendorResponse from(Vendor vendor) {
        return new VendorResponse(
                vendor.getId(),
                vendor.getCode(),
                vendor.getName(),
                vendor.getPhone(),
                vendor.getEmail(),
                vendor.getAddress());
    }
}
