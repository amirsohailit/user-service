package com.mytestorg.user.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

// TODO: For production, include Security Configurations (remove from excluded)
@SpringBootApplication(scanBasePackages = {MainApplication.BASE_PACKAGE}, exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@EnableReactiveMongoRepositories(basePackages = "com.mytestorg.user")
public class MainApplication {
    public static final String BASE_PACKAGE = "com.mytestorg.user";

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
