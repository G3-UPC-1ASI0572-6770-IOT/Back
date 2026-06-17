package com.parkingnow;

import com.parkingnow.shared.config.DatabaseUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParkingNowApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ParkingNowApplication.class);
        app.addInitializers(new DatabaseUrlConfig());
        app.run(args);
    }
}
