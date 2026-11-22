package com.simai.service;

import com.simai.common.exception.BusinessException;
import com.simai.common.exception.ResourceNotFoundException;
import com.simai.domain.Department;
import com.simai.domain.enums.AuditAction;
import com.simai.dto.master.DepartmentRequest;
import com.simai.dto.master.DepartmentResponse;
import com.simai.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAll() {
        return departmentRepository.findAll(Sort.by("name")).stream()
                .map(DepartmentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse findById(Long id) {
        return DepartmentResponse.from(getEntity(id));
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        ensureCodeAvailable(request.code(), null);

        Department department = new Department();
        department.setCode(request.code().trim());
        department.setName(request.name().trim());
        Department saved = departmentRepository.save(department);

        auditService.log("DEPARTMENT", String.valueOf(saved.getId()), AuditAction.CREATE, null, DepartmentResponse.from(saved));
        return DepartmentResponse.from(saved);
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = getEntity(id);
        DepartmentResponse oldValue = DepartmentResponse.from(department);
        ensureCodeAvailable(request.code(), id);

        department.setCode(request.code().trim());
        department.setName(request.name().trim());
        Department saved = departmentRepository.save(department);

        auditService.log("DEPARTMENT", String.valueOf(id), AuditAction.UPDATE, oldValue, DepartmentResponse.from(saved));
        return DepartmentResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Department department = getEntity(id);
        departmentRepository.delete(department);
        auditService.log("DEPARTMENT", String.valueOf(id), AuditAction.DELETE, DepartmentResponse.from(department), null);
    }

    private Department getEntity(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departemen", id));
    }

    private void ensureCodeAvailable(String code, Long excludeId) {
        departmentRepository.findByCode(code.trim()).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BusinessException("Kode departemen sudah digunakan");
            }
        });
    }
}
