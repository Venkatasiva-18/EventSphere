package com.example.EventSphere.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.EventSphere.model.Admin;
import com.example.EventSphere.repository.AdminRepository;
import com.example.EventSphere.service.AdminService;

@Component
public class AdminDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminDataInitializer.class);

    private final AdminRepository adminRepository;
    private final AdminService adminService;

    @Value("${app.admin.default-email:}")
    private String defaultEmail;

    @Value("${app.admin.default-password:}")
    private String defaultPassword;

    @Value("${app.admin.default-name:EventSphere Administrator}")
    private String defaultName;

    public AdminDataInitializer(AdminRepository adminRepository, AdminService adminService) {
        this.adminRepository = adminRepository;
        this.adminService = adminService;
    }

    @Override
    public void run(String... args) {
        if (!StringUtils.hasText(defaultEmail) || !StringUtils.hasText(defaultPassword)) {
            logger.info("Admin seeding skipped: default credentials not provided.");
            return;
        }

        if (adminRepository.existsByEmail(defaultEmail)) {
            logger.info("Admin seeding skipped: admin with email {} already exists.", defaultEmail);
            return;
        }

        Admin admin = new Admin();
        admin.setEmail(defaultEmail);
        admin.setPassword(defaultPassword);
        admin.setName(defaultName);
        admin.setEnabled(true);

        adminService.createAdmin(admin);
        logger.info("Default admin user created with email {}. Please change the password after first login.", defaultEmail);
    }
}