package com.agrosmart.agrosmartbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = "com.agrosmart")
@EnableJpaRepositories(basePackages = "com.agrosmart.repository")
@EntityScan(basePackages = "com.agrosmart.domain")
public class AgrosmartBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgrosmartBackendApplication.class, args);
    }
}