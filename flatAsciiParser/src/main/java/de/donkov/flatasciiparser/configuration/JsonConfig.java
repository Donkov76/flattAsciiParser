package de.donkov.flatasciiparser.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Provides the Jackson {@link ObjectMapper} as a bean. Spring Boot's Jackson
 * auto-configuration is not active here, so the mapper is created explicitly.
 */
@Configuration
public class JsonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
