package csd.tariff.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class BackendApplication {

    private static final Logger log = LoggerFactory.getLogger(BackendApplication.class);

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(BackendApplication.class, args);
        log.info("Tariff Calculation Backend Application started successfully");
    }

    @Component
    public static class StartupRunner implements CommandLineRunner {

        private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);

        @Override
        public void run(String... args) throws Exception {
            log.info("Application startup completed - Tariff Calculation Service is ready");
        }
    }
}
