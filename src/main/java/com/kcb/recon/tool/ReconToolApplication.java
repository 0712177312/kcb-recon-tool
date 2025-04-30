package com.kcb.recon.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kcb.recon.tool.common.services.InitializeAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableAsync
public class ReconToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReconToolApplication.class, args);
    }

    @Component
    public static class InitializeApplication implements CommandLineRunner {

        private final InitializeAppService initializeAppService;

        public InitializeApplication(InitializeAppService initializeAppService) {
            this.initializeAppService = initializeAppService;
        }

        @Override
        public void run(String... args) throws Exception {
            initializeAppService.InitializeApplication();
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }
}