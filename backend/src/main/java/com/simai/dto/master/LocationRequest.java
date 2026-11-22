package com.simai.dto.master;

import jakarta.validation.constraints.NotBlank;

public record LocationRequest(
        @NotBlank(message = "Kode wajib diisi") String code,
        @NotBlank(message = "Nama wajib diisi") String name,
        Long parentId,
        String address
) {
}
