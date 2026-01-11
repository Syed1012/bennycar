package de.bennycar.vehicle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * Main entry point for the Vehicle Service application.
 * This service manages vehicle catalog, customization options, and user configurations.
 */
@SpringBootApplication
@EnableFeignClients
@ComponentScan(basePackages = {"de.bennycar.vehicle", "de.bennycar.vehicle.mapper"})
public class VehicleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleServiceApplication.class, args);
    }
}

