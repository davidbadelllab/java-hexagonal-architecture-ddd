package com.davidbadell.hexagonal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Entry Point
 * 
 * Spring Boot application implementing Hexagonal Architecture with DDD.
 */
@SpringBootApplication
public class HexagonalApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(HexagonalApplication.class, args);
    }
}
