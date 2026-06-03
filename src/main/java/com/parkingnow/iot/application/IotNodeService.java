package com.parkingnow.iot.application;

import com.parkingnow.iot.application.dto.*;
import com.parkingnow.iot.domain.IotNode;
import com.parkingnow.iot.infrastructure.IotNodeRepository;
import com.parkingnow.shared.exception.ConflictException;
import com.parkingnow.shared.exception.NotFoundException;
import com.parkingnow.space.application.ParkingSpaceService;
import com.parkingnow.space.infrastructure.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IotNodeService {

    private final IotNodeRepository repo;
    private final ParkingSpaceRepository spaceRepo;
    private final ParkingSpaceService spaceService;

    @Transactional(readOnly = true)
    public List<IotNodeResponse> findAll() {
        return repo.findAll(PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "lastSeen")))
                .stream().map(IotNodeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<IotNodeResponse> findByLot(Long lotId) {
        return repo.findByLotId(lotId).stream().map(IotNodeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public IotNodeResponse findById(Long id) {
        return repo.findById(id).map(IotNodeResponse::from)
                .orElseThrow(() -> new NotFoundException("IoT node not found: " + id));
    }

    @Transactional(readOnly = true)
    public IotNodeResponse findByNodeCode(String nodeCode) {
        return repo.findByNodeCode(nodeCode)
                .map(IotNodeResponse::from)
                .orElseThrow(() -> new NotFoundException("IoT node not found: " + nodeCode));
    }

    @Transactional
    public IotNodeResponse register(IotNodeRequest req) {
        if (repo.findByNodeCode(req.getNodeCode()).isPresent()) {
            throw new ConflictException("Node already registered: " + req.getNodeCode());
        }
        IotNode node = IotNode.builder()
                .nodeCode(req.getNodeCode())
                .firmware(req.getFirmware() != null ? req.getFirmware() : "1.0.0")
                .spaceId(req.getSpaceId())
                .lotId(req.getLotId())
                .build();
        return IotNodeResponse.from(repo.save(node));
    }

    @Transactional
    public IotNodeResponse updateStatus(Long id, Map<String, String> body) {
        IotNode node = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("IoT node not found: " + id));
        String status = body.get("status");
        if (status != null) node.setStatus(IotNode.NodeStatus.valueOf(status.toUpperCase()));
        node.setLastSeen(Instant.now());
        return IotNodeResponse.from(repo.save(node));
    }

    @Transactional
    public IotNodeResponse heartbeat(String nodeCode) {
        IotNode node = repo.findByNodeCode(nodeCode)
                .orElseThrow(() -> new NotFoundException("Node not found: " + nodeCode));
        node.setLastSeen(Instant.now());
        node.setLastHeartbeatAt(Instant.now());
        node.setStatus(IotNode.NodeStatus.ONLINE);
        return IotNodeResponse.from(repo.save(node));
    }

    @Transactional
    public IotNodeResponse receiveHeartbeat(IotNodeRequest heartbeat) {
        var nodeOpt = repo.findByNodeCode(heartbeat.getNodeCode());
        IotNode node;
        if (nodeOpt.isPresent()) {
            node = nodeOpt.get();
        } else {
            node = IotNode.builder()
                    .nodeCode(heartbeat.getNodeCode())
                    .firmware(heartbeat.getFirmware() != null ? heartbeat.getFirmware() : "1.0.0")
                    .spaceId(heartbeat.getSpaceId())
                    .lotId(heartbeat.getLotId())
                    .build();
        }
        node.setLastSeen(Instant.now());
        node.setLastHeartbeatAt(Instant.now());
        node.setStatus(IotNode.NodeStatus.ONLINE);
        node.setUpdatedAt(Instant.now());
        return IotNodeResponse.from(repo.save(node));
    }

    @Transactional
    public void checkOfflineNodes(int offlineThresholdSeconds) {
        Instant threshold = Instant.now().minusSeconds(offlineThresholdSeconds);
        List<IotNode> nodes = repo.findAll();
        for (IotNode node : nodes) {
            if (node.getLastHeartbeatAt() != null && node.getLastHeartbeatAt().isBefore(threshold)) {
                node.setStatus(IotNode.NodeStatus.OFFLINE);
                repo.save(node);
                if (node.getSpaceId() != null) {
                    spaceRepo.findById(node.getSpaceId()).ifPresent(space -> {
                        spaceService.consolidateStatus(space);
                        spaceRepo.save(space);
                    });
                }
            }
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("IoT node not found: " + id);
        repo.deleteById(id);
    }
}
