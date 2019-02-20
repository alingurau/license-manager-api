package com.fortech.model.seeders;

import com.fortech.model.entity.LicenseType;
import com.fortech.model.entity.User;
import com.fortech.model.enums.Role;
import com.fortech.model.repository.LicenseTypeRepository;
import com.fortech.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LicenseTypeRepository licenseTypeRepository;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedUsersTable();
        seedLicenseTypesTable();
    }

    private void seedUsersTable() {
        adminSeedUser();
    }

    private void adminSeedUser() {
        String sql = "SELECT username FROM user u WHERE u.username = \"admin@license-manager.com\" LIMIT 1";
        List<User> u = jdbcTemplate.query(sql, (resultSet, rowNum) -> null);

        if (u == null || u.size() <= 0) {
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("License Manager");
            user.setUsername("admin@license-manager.com");
            user.setPassword(new BCryptPasswordEncoder().encode("admin1234"));
            user.setRole(Role.ADMIN);

            userRepository.save(user);
        }
    }

    private void seedLicenseTypesTable() {
        demoSeedLicenseType();
        trialSeedLicenseType();
        fullSeedLicenseType();
    }

    private void demoSeedLicenseType() {
        String demoSql = "SELECT description FROM license_type lt WHERE lt.description = \"DEMO\" LIMIT 1";
        List<LicenseType> lt = jdbcTemplate.query(demoSql, (resultSet, rowNum) -> null);

        if (lt == null || lt.size() <= 0) {
            LicenseType ltype = new LicenseType();
            ltype.setDescription("DEMO");
            ltype.setPeriodDays(30);

            licenseTypeRepository.save(ltype);
        }
    }

    private void trialSeedLicenseType() {
        String demoSql = "SELECT description FROM license_type lt WHERE lt.description = \"TRIAL\" LIMIT 1";
        List<LicenseType> lt = jdbcTemplate.query(demoSql, (resultSet, rowNum) -> null);

        if (lt == null || lt.size() <= 0) {
            LicenseType ltype = new LicenseType();
            ltype.setDescription("TRIAL");
            ltype.setPeriodDays(90);

            licenseTypeRepository.save(ltype);
        }
    }

    private void fullSeedLicenseType() {
        String demoSql = "SELECT description FROM license_type lt WHERE lt.description = \"FULL\" LIMIT 1";
        List<LicenseType> lt = jdbcTemplate.query(demoSql, (resultSet, rowNum) -> null);

        if (lt == null || lt.size() <= 0) {
            LicenseType ltype = new LicenseType();
            ltype.setDescription("FULL");
            ltype.setPeriodDays(365);

            licenseTypeRepository.save(ltype);
        }
    }
}
