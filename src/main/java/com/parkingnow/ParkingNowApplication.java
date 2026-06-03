package com.parkingnow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParkingNowApplication {
    public static void main(String[] args) {
        SpringApplication.run(ParkingNowApplication.class, args);
    }
}
