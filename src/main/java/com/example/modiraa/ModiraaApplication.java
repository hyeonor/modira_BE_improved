package com.example.modiraa;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableJpaAuditing
@SpringBootApplication
public class ModiraaApplication {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application-aws.yml,"
            + "classpath:application.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(ModiraaApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }
}
