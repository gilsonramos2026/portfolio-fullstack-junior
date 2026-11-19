package com.portifolio.api.config;

import com.portifolio.api.entity.AdminUser;
import com.portifolio.api.repository.AdminUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Garante a existência do usuário administrador no primeiro boot, com senha armazenada em BCrypt.[cite: 10]
 */
@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final AdminProperties properties;
    private final AdminUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrap(AdminProperties properties,
                          AdminUserRepository repository,
                          PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (repository.existsByUsername(properties.username())) {
            return;
        }
        repository.save(new AdminUser(
                properties.username(),
                passwordEncoder.encode(properties.password())
        ));
        log.info("Usuário administrador '{}' criado.", properties.username());
    }
}