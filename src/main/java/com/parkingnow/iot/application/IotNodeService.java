package com.parkingnow.iot.application;

import com.parkingnow.iot.application.dto.*;
import com.parkingnow.iot.domain.IotNode;
import com.parkingnow.iot.infrastructure.IotNodeRepository;
import com.parkingnow.shared.exception.ConflictException;
import com.parkingnow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IotNodeService {

    private final IotNodeRepository repo;

    public List<IotNodeResponse> findAll() {
        return repo.findAll().stream().map(IotNodeResponse::from).toList();
    }

    public List<IotNodeResponse> findByLot(Long lotId) {
        return repo.findByLotId(lotId).stream().map(IotNodeResponse::from).toList();
    }

    public IotNodeResponse findById(Long id) {
        return repo.findById(id).map(IotNodeResponse::from)
                .orElseThrow(() -> new NotFoundException("IoT node not found: " + id));
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

    // Called by ESP32 heartbeat
    @Transactional
    public IotNodeResponse heartbeat(String nodeCode) {
        IotNode node = repo.findByNodeCode(nodeCode)
                .orElseThrow(() -> new NotFoundException("Node not found: " + nodeCode));
        node.setLastSeen(Instant.now());
        node.setStatus(IotNode.NodeStatus.ONLINE);
        return IotNodeResponse.from(repo.save(node));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException("IoT node not found: " + id);
        repo.deleteById(id);
    }
}
