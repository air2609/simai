package com.simai.service;

import com.simai.common.exception.BusinessException;
import com.simai.common.exception.ResourceNotFoundException;
import com.simai.domain.Category;
import com.simai.domain.enums.AuditAction;
import com.simai.domain.enums.CategoryType;
import com.simai.dto.master.CategoryRequest;
import com.simai.dto.master.CategoryResponse;
import com.simai.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAll(CategoryType type) {
        if (type != null) {
            return categoryRepository.findByType(type).stream()
                    .map(CategoryResponse::from)
                    .toList();
        }
        return categoryRepository.findAll(Sort.by("name")).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        return CategoryResponse.from(getEntity(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        ensureCodeAvailable(request.code(), null);

        Category category = new Category();
        category.setCode(request.code().trim());
        category.setName(request.name().trim());
        category.setType(request.type());
        category.setUsefulLifeMonths(request.usefulLifeMonths());
        Category saved = categoryRepository.save(category);

        auditService.log("CATEGORY", String.valueOf(saved.getId()), AuditAction.CREATE, null, CategoryResponse.from(saved));
        return CategoryResponse.from(saved);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getEntity(id);
        CategoryResponse oldValue = CategoryResponse.from(category);
        ensureCodeAvailable(request.code(), id);

        category.setCode(request.code().trim());
        category.setName(request.name().trim());
        category.setType(request.type());
        category.setUsefulLifeMonths(request.usefulLifeMonths());
        Category saved = categoryRepository.save(category);

        auditService.log("CATEGORY", String.valueOf(id), AuditAction.UPDATE, oldValue, CategoryResponse.from(saved));
        return CategoryResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getEntity(id);
        categoryRepository.delete(category);
        auditService.log("CATEGORY", String.valueOf(id), AuditAction.DELETE, CategoryResponse.from(category), null);
    }

    private Category getEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori", id));
    }

    private void ensureCodeAvailable(String code, Long excludeId) {
        categoryRepository.findByCode(code.trim()).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BusinessException("Kode kategori sudah digunakan");
            }
        });
    }
}
