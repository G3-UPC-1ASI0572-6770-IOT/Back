package com.parkingnow.camera.presentation;

import com.parkingnow.camera.application.CameraFeedService;
import com.parkingnow.camera.application.dto.CameraFeedRequest;
import com.parkingnow.camera.application.dto.CameraFeedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/camera")
@RequiredArgsConstructor
public class CameraFeedController {

    private final CameraFeedService service;

    @Value("${app.iot.key:parkingnow-iot-key}")
    private String iotKey;

    @PostMapping("/snapshot/{parkingLotId}")
    public ResponseEntity<Map<String, Object>> saveSnapshot(
            @PathVariable Long parkingLotId,
            @RequestHeader(value = "X-IoT-Key", required = false) String key,
            @RequestBody(required = false) Map<String, String> body) {

        String imageUrl = body != null ? body.getOrDefault("imageUrl", "") : "";

        CameraFeedRequest req = new CameraFeedRequest();
        req.setParkingLotId(parkingLotId);
        req.setCameraUrl(imageUrl);
        req.setStatus("ONLINE");

        CameraFeedResponse saved = service.saveSnapshot(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "url", saved.getCameraUrl(),
                "timestamp", saved.getLastSeenAt().toEpochMilli()
        ));
    }

    @GetMapping("/snapshot/{parkingLotId}")
    public ResponseEntity<Map<String, Object>> getLatestSnapshot(@PathVariable Long parkingLotId) {
        CameraFeedResponse latest = service.findLatestByLot(parkingLotId);
        boolean isRecent = Instant.now().minusSeconds(30).isBefore(latest.getLastSeenAt());
        return ResponseEntity.ok(Map.of(
                "url", latest.getCameraUrl() != null ? latest.getCameraUrl() : "",
                "timestamp", latest.getLastSeenAt().toEpochMilli(),
                "isRecent", isRecent
        ));
    }
}
