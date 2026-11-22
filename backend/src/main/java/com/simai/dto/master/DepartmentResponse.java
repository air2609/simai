package com.simai.dto.master;

import com.simai.domain.Department;

public record DepartmentResponse(Long id, String code, String name) {

    public static DepartmentResponse from(Department department) {
        return new DepartmentResponse(department.getId(), department.getCode(), department.getName());
    }
}
