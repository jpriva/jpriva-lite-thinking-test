package com.jpriva.orders.config;

import com.jpriva.orders.domain.model.User;
import com.jpriva.orders.domain.model.vo.Role;
import com.jpriva.orders.domain.ports.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default.admin.email}")
    private String defaultAdminEmail;
    @Value("${app.default.admin.password}")
    private String defaultAdminPassword;
    @Value("${app.default.admin.name}")
    private String defaultAdminName;



    @Override
    public void run(String @NonNull ... args) throws Exception {
        log.info("Generating data...");
        if (userRepository.findByEmail(defaultAdminEmail).isEmpty()) {
            log.info("Creating default ADMIN...");
            createDefaultAdmin();

        }
        log.info("Inicialización de datos finalizada.");
    }
    private void createDefaultAdmin(){
        String password = defaultAdminPassword;
        String passwordHashed = passwordEncoder.encode(password);
        User user = User.create(defaultAdminEmail,passwordHashed,defaultAdminName,null,null, Role.ADMIN);
        userRepository.save(user);
    }
}
