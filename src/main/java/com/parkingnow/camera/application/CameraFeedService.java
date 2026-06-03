package com.parkingnow.camera.application;

import com.parkingnow.camera.application.dto.CameraFeedRequest;
import com.parkingnow.camera.application.dto.CameraFeedResponse;
import com.parkingnow.camera.domain.CameraFeed;
import com.parkingnow.camera.infrastructure.CameraFeedRepository;
import com.parkingnow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CameraFeedService {

    private final CameraFeedRepository repo;

    @Transactional
    public CameraFeedResponse saveSnapshot(CameraFeedRequest req) {
        return register(req);
    }

    @Transactional
    public CameraFeedResponse register(CameraFeedRequest req) {
        CameraFeed feed = CameraFeed.builder()
                .parkingLotId(req.getParkingLotId())
                .nodeId(req.getNodeId())
                .cameraUrl(req.getCameraUrl())
                .status(parseStatus(req.getStatus()))
                .lastSeenAt(Instant.now())
                .build();
        return CameraFeedResponse.from(repo.save(feed));
    }

    @Transactional(readOnly = true)
    public List<CameraFeedResponse> findByLot(Long parkingLotId) {
        return repo.findByParkingLotId(parkingLotId).stream()
                .map(CameraFeedResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CameraFeedResponse findLatestByLot(Long parkingLotId) {
        return repo.findFirstByParkingLotIdOrderByLastSeenAtDesc(parkingLotId)
                .map(CameraFeedResponse::from)
                .orElseThrow(() -> new NotFoundException("No camera feed for lot: " + parkingLotId));
    }

    @Transactional
    public CameraFeedResponse updateStatus(Long id, String status) {
        CameraFeed feed = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Camera feed not found: " + id));
        feed.setStatus(parseStatus(status));
        feed.setLastSeenAt(Instant.now());
        return CameraFeedResponse.from(repo.save(feed));
    }

    private CameraFeed.CameraStatus parseStatus(String s) {
        if (s == null) return CameraFeed.CameraStatus.ONLINE;
        try {
            return CameraFeed.CameraStatus.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CameraFeed.CameraStatus.ONLINE;
        }
    }
}
