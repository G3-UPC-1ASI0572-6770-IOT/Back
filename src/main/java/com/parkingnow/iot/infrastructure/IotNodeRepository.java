package com.parkingnow.iot.infrastructure;

import com.parkingnow.iot.domain.IotNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IotNodeRepository extends JpaRepository<IotNode, Long> {
    List<IotNode> findByLotId(Long lotId);
    Optional<IotNode> findByNodeCode(String nodeCode);
    int countByLotId(Long lotId);
}
