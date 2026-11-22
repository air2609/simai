package com.simai.service;

import com.simai.common.exception.BusinessException;
import com.simai.common.exception.ResourceNotFoundException;
import com.simai.domain.Department;
import com.simai.domain.User;
import com.simai.domain.enums.AuditAction;
import com.simai.domain.enums.Role;
import com.simai.dto.master.UserRequest;
import com.simai.dto.master.UserResponse;
import com.simai.repository.DepartmentRepository;
import com.simai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll(Sort.by("name")).stream()
                .map(UserResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long id) {
        return UserResponse.from(getEntity(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (!StringUtils.hasText(request.password())) {
            throw new BusinessException("Password wajib diisi");
        }
        ensureEmailAvailable(request.email(), null);

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setDepartment(resolveDepartment(request.departmentId()));
        user.setActive(request.active() == null || request.active());
        User saved = userRepository.save(user);

        auditService.log("USER", String.valueOf(saved.getId()), AuditAction.CREATE, null, UserResponse.from(saved));
        return UserResponse.from(saved);
    }

    @Transactional
    public UserResponse update(Long id, UserRequest request) {
        User user = getEntity(id);
        UserResponse oldValue = UserResponse.from(user);
        ensureEmailAvailable(request.email(), id);

        user.setName(request.name().trim());
        user.setEmail(request.email().trim().toLowerCase());
        if (StringUtils.hasText(request.password())) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        user.setRole(request.role());
        user.setDepartment(resolveDepartment(request.departmentId()));
        user.setActive(request.active() == null || request.active());
        User saved = userRepository.save(user);

        auditService.log("USER", String.valueOf(id), AuditAction.UPDATE, oldValue, UserResponse.from(saved));
        return UserResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        User user = getEntity(id);

        if (user.getId().equals(currentUserService.currentUserId())) {
            throw new BusinessException("Tidak dapat menghapus akun yang sedang digunakan");
        }
        if (user.getRole() == Role.ADMIN && userRepository.countByRoleAndActiveTrue(Role.ADMIN) <= 1) {
            throw new BusinessException("Tidak dapat menghapus satu-satunya admin aktif");
        }

        userRepository.delete(user);
        auditService.log("USER", String.valueOf(id), AuditAction.DELETE, UserResponse.from(user), null);
    }

    private User getEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pengguna", id));
    }

    private void ensureEmailAvailable(String email, Long excludeId) {
        userRepository.findByEmailIgnoreCase(email.trim()).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BusinessException("Email sudah digunakan");
            }
        });
    }

    private Department resolveDepartment(Long departmentId) {
        if (departmentId == null) {
            return null;
        }
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Departemen", departmentId));
    }
}
