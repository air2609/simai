package com.simai.dto.master;

import com.simai.domain.Category;

public record CategoryResponse(
        Long id,
        String code,
        String name,
        String type,
        Integer usefulLifeMonths
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getType().name(),
                category.getUsefulLifeMonths());
    }
}
