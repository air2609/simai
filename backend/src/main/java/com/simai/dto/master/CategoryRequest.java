package com.simai.dto.master;

import com.simai.domain.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @NotBlank(message = "Kode wajib diisi") String code,
        @NotBlank(message = "Nama wajib diisi") String name,
        @NotNull(message = "Tipe wajib diisi") CategoryType type,
        Integer usefulLifeMonths
) {
}
