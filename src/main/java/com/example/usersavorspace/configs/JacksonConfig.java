package com.example.usersavorspace.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.GrantedAuthority;

@Configuration
public class JacksonConfig {
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

        SimpleModule module = new SimpleModule();
        module.addSerializer(GrantedAuthority.class, new GrantedAuthoritySerializer());


        builder.modules(new JavaTimeModule(), module);
        return builder;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return jacksonBuilder().build();
    }
}