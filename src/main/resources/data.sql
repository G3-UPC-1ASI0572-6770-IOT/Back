-- Seed: OWNER user (password: demo1234)
INSERT INTO users (full_name, email, password, phone, job_title, role, active, two_factor_enabled, created_at)
VALUES (
  'Alex Johnson',
  'owner@parkingnow.com',
  '$2a$10$XojH8HaHiGQUVUmS6/mQRuMgGDGTy5oIXJExkIl0S/6qSiNnVRqfu',
  '+51 999 888 777',
  'Facility Manager',
  'OWNER',
  true,
  false,
  CURRENT_TIMESTAMP
);

-- Seed: DRIVER user (password: demo1234)
INSERT INTO users (full_name, email, password, phone, job_title, role, active, two_factor_enabled, created_at)
VALUES (
  'Maria Torres',
  'driver@parkingnow.com',
  '$2a$10$XojH8HaHiGQUVUmS6/mQRuMgGDGTy5oIXJExkIl0S/6qSiNnVRqfu',
  '+51 987 001 001',
  'Conductor',
  'DRIVER',
  true,
  false,
  CURRENT_TIMESTAMP
);

-- Seed: parking lot (owner_id = 1)
INSERT INTO parking_lots (name, address, city, capacity, hourly_rate, lot_type, owner_id, latitude, longitude, node_id, created_at)
VALUES (
  'ParkingNow San Isidro',
  'Av. Javier Prado Este 1234',
  'San Isidro, Lima',
  10,
  3.00,
  'open',
  1,
  -12.1016,
  -77.0355,
  'NODE_01',
  CURRENT_TIMESTAMP
);

-- Seed: parking spaces (E1, E2 per plan)
INSERT INTO parking_spaces (code, zone, type, physical_status, logical_status, consolidated_status, lot_id, created_at, updated_at)
VALUES ('E1', 'Zone A', 'Standard', 'AVAILABLE', 'AVAILABLE', 'AVAILABLE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO parking_spaces (code, zone, type, physical_status, logical_status, consolidated_status, lot_id, created_at, updated_at)
VALUES ('E2', 'Zone A', 'Standard', 'AVAILABLE', 'AVAILABLE', 'AVAILABLE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed: IoT node linked to lot
INSERT INTO iot_nodes (node_code, firmware, status, last_seen, last_heartbeat_at, space_id, lot_id, created_at, updated_at)
VALUES ('NODE_01', '1.0.0', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed: sample reservation (ACTIVE)
INSERT INTO reservations (space_id, space_label, lot_id, parking_lot_name, driver_id, driver_email, status, expires_at, created_at)
VALUES (1, 'E1', 1, 'ParkingNow San Isidro', 2, 'driver@parkingnow.com', 'ACTIVE', DATEADD('MINUTE', 15, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP);

-- Seed: IoT events
INSERT INTO iot_events (node_id, parking_space_id, space_code, distance_cm, detected_status, received_at, synced_at, created_at)
VALUES ('NODE_01', 1, 'E1', 35.5, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO iot_events (node_id, parking_space_id, space_code, distance_cm, detected_status, received_at, synced_at, created_at)
VALUES ('NODE_01', 2, 'E2', 8.2, 'OCCUPIED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed: camera feed
INSERT INTO camera_feeds (parking_lot_id, node_id, camera_url, status, last_seen_at, created_at)
VALUES (1, 'NODE_01', 'http://192.168.1.100/snapshot', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
