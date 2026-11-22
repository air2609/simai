package com.simai.dto.master;

import com.simai.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotBlank(message = "Nama wajib diisi") String name,
        @NotBlank(message = "Email wajib diisi") @Email(message = "Format email tidak valid") String email,
        String password,
        @NotNull(message = "Role wajib diisi") Role role,
        Long departmentId,
        Boolean active
) {
}
