package com.simai.dto.master;

import com.simai.domain.Location;

public record LocationResponse(
        Long id,
        String code,
        String name,
        Long parentId,
        String parentName,
        String address
) {

    public static LocationResponse from(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getCode(),
                location.getName(),
                location.getParent() == null ? null : location.getParent().getId(),
                location.getParent() == null ? null : location.getParent().getName(),
                location.getAddress());
    }
}
