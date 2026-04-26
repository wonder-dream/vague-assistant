package com.vague.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(scanBasePackages = "com.vague")
public class VagueWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(VagueWebApplication.class, args);
    }
}
