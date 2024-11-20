package com.printshop.config;

import com.printshop.model.User;
import com.printshop.model.UserRole;
import com.printshop.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initializeAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setFullName("System Administrator");
                adminUser.setRole(UserRole.ADMIN);
                adminUser.setActive(true);
                userRepository.save(adminUser);
            }
        };
    }
}