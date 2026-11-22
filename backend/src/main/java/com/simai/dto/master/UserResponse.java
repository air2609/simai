package com.simai.dto.master;

import com.simai.domain.User;

public record UserResponse(
        Long id,
        String name,
        String email,
        String role,
        Long departmentId,
        String departmentName,
        boolean active
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getDepartment() == null ? null : user.getDepartment().getId(),
                user.getDepartment() == null ? null : user.getDepartment().getName(),
                user.isActive());
    }
}
