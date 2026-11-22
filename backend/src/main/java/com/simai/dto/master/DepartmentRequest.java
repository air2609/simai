package com.simai.dto.master;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequest(
        @NotBlank(message = "Kode wajib diisi") String code,
        @NotBlank(message = "Nama wajib diisi") String name
) {
}
