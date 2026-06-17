package com.parkingnow.camera.presentation;

import com.parkingnow.camera.application.CameraFeedService;
import com.parkingnow.camera.application.dto.CameraFeedRequest;
import com.parkingnow.camera.application.dto.CameraFeedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/camera")
@RequiredArgsConstructor
public class CameraFeedController {

    private final CameraFeedService service;

    @Value("${app.iot.key:parkingnow-iot-key}")
    private String iotKey;

    /**
     * Receives a snapshot from the Edge/ESP32-CAM.
     *
     * Accepts multipart/form-data with field "image" (raw JPEG bytes, as sent
     * by the Edge), and stores it as a base64 data URI so the web client can
     * render it directly in an <img> tag. Falls back to a JSON { imageUrl }
     * body for clients that only have an external URL.
     */
    @PostMapping(value = "/snapshot/{parkingLotId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,
                        MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Map<String, Object>> saveSnapshotMultipart(
            @PathVariable Long parkingLotId,
            @RequestHeader(value = "X-IoT-Key", required = false) String key,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        validateKey(key);

        String dataUri = "";
        if (image != null && !image.isEmpty()) {
            try {
                String b64 = Base64.getEncoder().encodeToString(image.getBytes());
                dataUri = "data:image/jpeg;base64," + b64;
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot read uploaded image: " + e.getMessage());
            }
        }
        return store(parkingLotId, dataUri);
    }

    @PostMapping(value = "/snapshot/{parkingLotId}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> saveSnapshotJson(
            @PathVariable Long parkingLotId,
            @RequestHeader(value = "X-IoT-Key", required = false) String key,
            @RequestBody(required = false) Map<String, String> body) {
        validateKey(key);
        String imageUrl = body != null ? body.getOrDefault("imageUrl", "") : "";
        return store(parkingLotId, imageUrl);
    }

    private ResponseEntity<Map<String, Object>> store(Long parkingLotId, String url) {
        CameraFeedRequest req = new CameraFeedRequest();
        req.setParkingLotId(parkingLotId);
        req.setCameraUrl(url);
        req.setStatus("ONLINE");

        CameraFeedResponse saved = service.saveSnapshot(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "url", saved.getCameraUrl(),
                "timestamp", saved.getLastSeenAt().toEpochMilli()
        ));
    }

    private void validateKey(String key) {
        if (key == null || !iotKey.equals(key)) {
            throw new org.springframework.security.access.AccessDeniedException("Invalid or missing IoT key");
        }
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
