package com.example.crewconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CrewConnectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrewConnectApplication.class, args);
    }
}
