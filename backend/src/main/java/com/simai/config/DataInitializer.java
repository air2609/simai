package com.simai.config;

import com.simai.domain.Category;
import com.simai.domain.Department;
import com.simai.domain.Location;
import com.simai.domain.User;
import com.simai.domain.enums.CategoryType;
import com.simai.domain.enums.Role;
import com.simai.repository.CategoryRepository;
import com.simai.repository.DepartmentRepository;
import com.simai.repository.LocationRepository;
import com.simai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Department defaultDept = departmentRepository.findByCode("DFLT")
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setCode("DFLT");
                    d.setName("Default");
                    return departmentRepository.save(d);
                });

        locationRepository.findByCode("WH-01").orElseGet(() -> {
            Location l = new Location();
            l.setCode("WH-01");
            l.setName("Gudang Utama");
            return locationRepository.save(l);
        });

        categoryRepository.findByCode("CAT-ASSET").orElseGet(() -> {
            Category c = new Category();
            c.setCode("CAT-ASSET");
            c.setName("Aset Umum");
            c.setType(CategoryType.ASSET);
            return categoryRepository.save(c);
        });

        categoryRepository.findByCode("CAT-INV").orElseGet(() -> {
            Category c = new Category();
            c.setCode("CAT-INV");
            c.setName("Inventori Umum");
            c.setType(CategoryType.INVENTORY);
            return categoryRepository.save(c);
        });

        if (!userRepository.existsByEmailIgnoreCase("admin@simai.local")) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setEmail("admin@simai.local");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setDepartment(defaultDept);
            admin.setActive(true);
            userRepository.save(admin);
        }
    }
}
