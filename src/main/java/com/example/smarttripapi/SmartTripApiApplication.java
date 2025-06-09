package com.example.smarttripapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SmartTripApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTripApiApplication.class, args);
    }

}
