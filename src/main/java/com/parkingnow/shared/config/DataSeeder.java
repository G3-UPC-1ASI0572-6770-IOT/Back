package com.parkingnow.shared.config;

import com.parkingnow.camera.domain.CameraFeed;
import com.parkingnow.camera.infrastructure.CameraFeedRepository;
import com.parkingnow.iam.application.AuthService;
import com.parkingnow.iam.application.dto.SignUpRequest;
import com.parkingnow.iam.infrastructure.UserRepository;
import com.parkingnow.iot.domain.IotEvent;
import com.parkingnow.iot.domain.IotNode;
import com.parkingnow.iot.infrastructure.IotEventRepository;
import com.parkingnow.iot.infrastructure.IotNodeRepository;
import com.parkingnow.parkinglot.domain.ParkingLot;
import com.parkingnow.parkinglot.infrastructure.ParkingLotRepository;
import com.parkingnow.reservation.domain.Reservation;
import com.parkingnow.reservation.infrastructure.ReservationRepository;
import com.parkingnow.space.domain.ParkingSpace;
import com.parkingnow.space.infrastructure.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final IotNodeRepository iotNodeRepository;
    private final ReservationRepository reservationRepository;
    private final IotEventRepository iotEventRepository;
    private final CameraFeedRepository cameraFeedRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        installOwner();
        var owner = userRepository.findByEmail("owner@parkingnow.com").orElseThrow();
        owner.setPhone("+51 999 888 777");
        owner.setJobTitle("Facility Manager");
        userRepository.save(owner);

        installDriver();
        var driver = userRepository.findByEmail("driver@parkingnow.com").orElseThrow();
        driver.setPhone("+51 987 001 001");
        driver.setJobTitle("Conductor");
        userRepository.save(driver);

        var lot = parkingLotRepository.save(ParkingLot.builder()
                .name("ParkingNow San Isidro")
                .address("Av. Javier Prado Este 1234")
                .city("San Isidro, Lima")
                .capacity(10)
                .hourlyRate(BigDecimal.valueOf(3.00))
                .lotType("open")
                .ownerId(owner.getId())
                .latitude(-12.1016)
                .longitude(-77.0355)
                .nodeId("NODE_01")
                .build());

        var space1 = parkingSpaceRepository.save(ParkingSpace.builder()
                .code("E1").zone("Zone A").type("Standard")
                .physicalStatus(ParkingSpace.PhysicalStatus.AVAILABLE)
                .logicalStatus(ParkingSpace.LogicalStatus.AVAILABLE)
                .consolidatedStatus(ParkingSpace.ConsolidatedStatus.AVAILABLE)
                .lotId(lot.getId())
                .build());

        var space2 = parkingSpaceRepository.save(ParkingSpace.builder()
                .code("E2").zone("Zone A").type("Standard")
                .physicalStatus(ParkingSpace.PhysicalStatus.AVAILABLE)
                .logicalStatus(ParkingSpace.LogicalStatus.AVAILABLE)
                .consolidatedStatus(ParkingSpace.ConsolidatedStatus.AVAILABLE)
                .lotId(lot.getId())
                .build());

        var node = iotNodeRepository.save(IotNode.builder()
                .nodeCode("NODE_01")
                .firmware("1.0.0")
                .status(IotNode.NodeStatus.ONLINE)
                .spaceId(space1.getId())
                .lotId(lot.getId())
                .build());

        reservationRepository.save(Reservation.builder()
                .spaceId(space1.getId())
                .spaceLabel("E1")
                .lotId(lot.getId())
                .parkingLotName("ParkingNow San Isidro")
                .driverId(driver.getId())
                .driverEmail("driver@parkingnow.com")
                .status(Reservation.ReservationStatus.ACTIVE)
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build());

        iotEventRepository.save(IotEvent.builder()
                .nodeId("NODE_01")
                .parkingSpaceId(space1.getId())
                .spaceCode("E1")
                .distanceCm(35.5)
                .detectedStatus(IotEvent.DetectedStatus.AVAILABLE)
                .build());

        iotEventRepository.save(IotEvent.builder()
                .nodeId("NODE_01")
                .parkingSpaceId(space2.getId())
                .spaceCode("E2")
                .distanceCm(8.2)
                .detectedStatus(IotEvent.DetectedStatus.OCCUPIED)
                .build());

        cameraFeedRepository.save(CameraFeed.builder()
                .parkingLotId(lot.getId())
                .nodeId("NODE_01")
                .cameraUrl("http://192.168.1.100/snapshot")
                .status(CameraFeed.CameraStatus.ONLINE)
                .build());
    }

    private void installOwner() {
        var req = new SignUpRequest();
        req.setName("Alex Johnson");
        req.setEmail("owner@parkingnow.com");
        req.setPassword("demo1234");
        authService.signUpOwner(req);
    }

    private void installDriver() {
        var req = new SignUpRequest();
        req.setName("Maria Torres");
        req.setEmail("driver@parkingnow.com");
        req.setPassword("demo1234");
        authService.signUpDriver(req);
    }
}
