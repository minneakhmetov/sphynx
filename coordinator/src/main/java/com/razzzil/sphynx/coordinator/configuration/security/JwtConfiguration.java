package com.razzzil.sphynx.coordinator.configuration.security;

import com.razzzil.sphynx.coordinator.model.jwt.JwtConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class JwtConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "spring.security.jwt")
    public JwtConfig jwtConfig(){
        return new JwtConfig();
    }

}
