package com.simai.dto.master;

import jakarta.validation.constraints.NotBlank;

public record VendorRequest(
        @NotBlank(message = "Kode wajib diisi") String code,
        @NotBlank(message = "Nama wajib diisi") String name,
        String phone,
        String email,
        String address
) {
}
