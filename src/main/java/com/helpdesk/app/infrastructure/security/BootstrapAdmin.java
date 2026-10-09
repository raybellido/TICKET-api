package com.helpdesk.app.infrastructure.security;

import com.helpdesk.app.application.port.out.PasswordEncoder;
import com.helpdesk.app.application.port.out.UserRepository;
import com.helpdesk.app.domain.model.user.Email;
import com.helpdesk.app.domain.model.user.User;
import com.helpdesk.app.domain.model.user.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@EnableConfigurationProperties(BootstrapAdminProperties.class)
public class BootstrapAdmin implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdmin.class);

    private final BootstrapAdminProperties properties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public BootstrapAdmin(
            BootstrapAdminProperties properties,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.properties = properties;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (!properties.enabled()) {
            return;
        }

        Optional<User> existing = userRepository.findByEmail(properties.email());

        if (existing.isPresent()) {
            User user = existing.get();
            if (user.getRole() == UserRole.ADMIN) {
                return;
            }
            user.changeRole(UserRole.ADMIN);
            userRepository.save(user);
            log.info("Usuario bootstrap {} promocionado a ADMIN", properties.email());
            return;
        }

        User admin = new User(
                properties.name(),
                new Email(properties.email()),
                passwordEncoder.encode(properties.password())
        );

        admin.changeRole(UserRole.ADMIN);

        userRepository.save(admin);
        log.info("Usuario ADMIN bootstrap creado: {}", properties.email());
    }
}
