package com.library.config;

import com.library.model.Book;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class LibraryConfig {

    @Value("${jwt.secretKey}")
    private String secretKeyString;

    @Bean
    public SecretKey secretKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    @Bean
    public Map<String, Book> books() {
        return new ConcurrentHashMap<>();
    }

}
