package com.fortech.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(scanBasePackages = {"com.fortech.model.security","com.fortech.restapiimpl"})
@EntityScan(basePackages = "com.fortech.model")
//@ComponentScan({"com.fortech" })
@EnableJpaRepositories(basePackages = {"com.fortech.model.repository"})
public class BootApplication {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }
}
