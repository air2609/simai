package com.simai.dto.auth;

public record LoginResponse(
        String token,
        String tokenType,
        UserResponse user
) {
}
