package com.simai.dto.auth;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role
) {
}
