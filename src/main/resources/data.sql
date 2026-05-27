-- Seed: demo admin user (password: demo1234)
INSERT INTO users (full_name, email, password, phone, job_title, role, active, two_factor_enabled, created_at)
VALUES (
  'Alex Johnson',
  'admin@parkingnow.com',
  '$2a$10$XojH8HaHiGQUVUmS6/mQRuMgGDGTy5oIXJExkIl0S/6qSiNnVRqfu',
  '+51 999 888 777',
  'Facility Manager',
  'ADMIN',
  true,
  false,
  CURRENT_TIMESTAMP
);

-- Seed: demo parking lot
INSERT INTO parking_lots (name, address, city, capacity, hourly_rate, status, lot_type, owner_id, rating, created_at)
VALUES (
  'ParkingNow San Isidro',
  'Av. Javier Prado Este 1234',
  'San Isidro, Lima',
  40,
  4.50,
  'AVAILABLE',
  'open',
  1,
  4.7,
  CURRENT_TIMESTAMP
);

-- Seed: parking spaces (Zone A - 10 spaces)
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('A01','Zone A','Standard','ESP32-001','AVAILABLE',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('A02','Zone A','Compact','ESP32-002','OCCUPIED',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('A03','Zone A','EV','ESP32-003','AVAILABLE',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('A04','Zone A','Standard','ESP32-004','RESERVED',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('A05','Zone A','Accessible','ESP32-005','AVAILABLE',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('A06','Zone A','Standard','ESP32-006','OCCUPIED',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('A07','Zone A','Compact','ESP32-007','OFFLINE',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('A08','Zone A','Standard','ESP32-008','AVAILABLE',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('A09','Zone A','EV','ESP32-009','AVAILABLE',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('A10','Zone A','Standard','ESP32-010','OCCUPIED',1);

-- Seed: Zone B
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('B01','Zone B','Standard','ESP32-011','AVAILABLE',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('B02','Zone B','Compact','ESP32-012','OCCUPIED',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('B03','Zone B','Standard','ESP32-013','AVAILABLE',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('B04','Zone B','Accessible','ESP32-014','RESERVED',1);
INSERT INTO parking_spaces (code, zone, type, sensor_code, status, lot_id) VALUES ('B05','Zone B','Standard','ESP32-015','AVAILABLE',1);

-- Seed: IoT nodes
INSERT INTO iot_nodes (node_code, firmware, status, last_seen, space_id, lot_id) VALUES ('ESP32-001','2.1.0','ONLINE',CURRENT_TIMESTAMP,1,1);
INSERT INTO iot_nodes (node_code, firmware, status, last_seen, space_id, lot_id) VALUES ('ESP32-002','2.1.0','ONLINE',CURRENT_TIMESTAMP,2,1);
INSERT INTO iot_nodes (node_code, firmware, status, last_seen, space_id, lot_id) VALUES ('ESP32-003','2.0.5','ONLINE',CURRENT_TIMESTAMP,3,1);
INSERT INTO iot_nodes (node_code, firmware, status, last_seen, space_id, lot_id) VALUES ('ESP32-004','2.1.0','ONLINE',CURRENT_TIMESTAMP,4,1);
INSERT INTO iot_nodes (node_code, firmware, status, last_seen, space_id, lot_id) VALUES ('ESP32-005','1.9.2','WARNING',CURRENT_TIMESTAMP,5,1);
INSERT INTO iot_nodes (node_code, firmware, status, last_seen, space_id, lot_id) VALUES ('ESP32-006','2.1.0','ONLINE',CURRENT_TIMESTAMP,6,1);
INSERT INTO iot_nodes (node_code, firmware, status, last_seen, space_id, lot_id) VALUES ('ESP32-007','2.0.0','OFFLINE',CURRENT_TIMESTAMP,7,1);

-- Seed: reservations
INSERT INTO reservations (code, driver_name, driver_phone, space_id, lot_id, start_time, end_time, status, created_at) VALUES ('RS-2201','Maria Torres','+51 987 001 001',4,1,'09:00','11:00','ACTIVE',CURRENT_TIMESTAMP);
INSERT INTO reservations (code, driver_name, driver_phone, space_id, lot_id, start_time, end_time, status, created_at) VALUES ('RS-2202','Carlos Diaz','+51 987 002 002',11,1,'10:30','12:30','ACTIVE',CURRENT_TIMESTAMP);
INSERT INTO reservations (code, driver_name, driver_phone, space_id, lot_id, start_time, end_time, status, created_at) VALUES ('RS-2203','Priya Singh','+51 987 003 003',2,1,'08:00','09:00','FINISHED',CURRENT_TIMESTAMP);
INSERT INTO reservations (code, driver_name, driver_phone, space_id, lot_id, start_time, end_time, status, created_at) VALUES ('RS-2204','Liam Brown','+51 987 004 004',3,1,'07:30','08:30','CANCELLED',CURRENT_TIMESTAMP);

-- Seed: event alerts
INSERT INTO event_alerts (severity, title, message, lot_id, space_id, node_id, created_at) VALUES ('CRITICAL','IoT node disconnected','Heartbeat lost on ESP32-007 — Lot 1, Space A07',1,7,7,CURRENT_TIMESTAMP);
INSERT INTO event_alerts (severity, title, message, lot_id, space_id, node_id, created_at) VALUES ('INFO','Vehicle entry registered','Boom gate cycle complete — Space A02',1,2,2,CURRENT_TIMESTAMP);
INSERT INTO event_alerts (severity, title, message, lot_id, space_id, node_id, created_at) VALUES ('RESOLVED','Reservation RS-2201 confirmed','New booking via admin panel — Space A04',1,4,4,CURRENT_TIMESTAMP);
INSERT INTO event_alerts (severity, title, message, lot_id, space_id, node_id, created_at) VALUES ('WARNING','Low battery on ESP32-005','Battery dropped below 25% — Space A05',1,5,5,CURRENT_TIMESTAMP);
INSERT INTO event_alerts (severity, title, message, lot_id, space_id, node_id, created_at) VALUES ('INFO','Sensor calibration complete','Routine check passed — Zone A all nodes',1,null,1,CURRENT_TIMESTAMP);

-- Seed: payments (RS-2203 is FINISHED so it has a payment)
INSERT INTO payments (reservation_id, amount, currency, method, status, paid_at, created_at) VALUES (3, 8.50, 'PEN', 'DEMO_CARD', 'PAID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
INSERT INTO payments (reservation_id, amount, currency, method, status, paid_at, created_at) VALUES (1, 12.00, 'PEN', 'YAPE_DEMO', 'PAID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed: tickets
INSERT INTO tickets (reservation_id, ticket_code, qr_payload, qr_url, status, created_at) VALUES (3, 'TKT-DEMO0001', 'PARKINGNOW|RES:3|CODE:TKT-DEMO0001', 'https://api.qrserver.com/v1/create-qr-code/?data=PARKINGNOW%7CRES%3A3%7CCODE%3ATKT-DEMO0001&size=200x200', 'USED', CURRENT_TIMESTAMP);
INSERT INTO tickets (reservation_id, ticket_code, qr_payload, qr_url, status, created_at) VALUES (1, 'TKT-DEMO0002', 'PARKINGNOW|RES:1|CODE:TKT-DEMO0002', 'https://api.qrserver.com/v1/create-qr-code/?data=PARKINGNOW%7CRES%3A1%7CCODE%3ATKT-DEMO0002&size=200x200', 'ACTIVE', CURRENT_TIMESTAMP);

-- Seed: camera feeds
INSERT INTO camera_feeds (parking_lot_id, node_id, camera_url, status, last_seen_at, created_at) VALUES (1, 'ESP32-CAM-001', 'http://192.168.1.25/stream', 'ONLINE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
