-- =====================================================
-- BusApp Database - DDL Script
-- MySQL 8.0+
-- =====================================================

CREATE DATABASE IF NOT EXISTS busapp_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE busapp_db;

-- =====================================================
-- Table: roles
-- =====================================================
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    CONSTRAINT uk_roles_role_name UNIQUE (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default roles
INSERT INTO roles (role_name, description) VALUES
    ('customer', 'Khách hàng'),
    ('operator', 'Nhà xe'),
    ('admin', 'Quản trị viên')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- =====================================================
-- Table: users
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    avatar_url VARCHAR(500),
    role_id INT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: provinces
-- =====================================================
CREATE TABLE IF NOT EXISTS provinces (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    CONSTRAINT uk_provinces_name UNIQUE (name),
    CONSTRAINT uk_provinces_slug UNIQUE (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: vehicle_types
-- =====================================================
CREATE TABLE IF NOT EXISTS vehicle_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    seat_count INT NOT NULL,
    seat_layout VARCHAR(20),
    floor_count INT NOT NULL DEFAULT 1,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert default vehicle types
INSERT INTO vehicle_types (name, seat_count, seat_layout, floor_count, description) VALUES
    ('Ghế 32 chỗ', 32, '2-2', 1, 'Xe ghế 32 chỗ thường'),
    ('Ghế 45 chỗ', 45, '2-2', 1, 'Xe ghế 45 chỗ'),
    ('Giường 34 chỗ', 34, '1-1', 2, 'Xe giường 34 chỗ 2 tầng'),
    ('Giường 40 chỗ', 40, '1-1', 2, 'Xe giường 40 chỗ 2 tầng'),
    ('Limousine 24 chỗ', 24, '1-1', 1, 'Xe Limousine 24 chỗ VIP')
ON DUPLICATE KEY UPDATE description = VALUES(description);

-- =====================================================
-- Table: bus_operators
-- =====================================================
CREATE TABLE IF NOT EXISTS bus_operators (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    description TEXT,
    logo_url VARCHAR(500),
    amenities VARCHAR(500),
    cancellation_policy TEXT,
    avg_rating FLOAT DEFAULT 0.0,
    total_reviews INT DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: routes
-- =====================================================
CREATE TABLE IF NOT EXISTS routes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    departure_province_id INT NOT NULL,
    destination_province_id INT NOT NULL,
    distance_km INT,
    duration_hours FLOAT,
    is_popular BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_routes_departure FOREIGN KEY (departure_province_id) REFERENCES provinces(id),
    CONSTRAINT fk_routes_destination FOREIGN KEY (destination_province_id) REFERENCES provinces(id),
    CONSTRAINT uk_routes_provinces UNIQUE (departure_province_id, destination_province_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: pickup_dropoff_points
-- =====================================================
CREATE TABLE IF NOT EXISTS pickup_dropoff_points (
    id INT AUTO_INCREMENT PRIMARY KEY,
    operator_id INT NOT NULL,
    route_id INT NOT NULL,
    point_type ENUM('pickup', 'dropoff') NOT NULL,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(300),
    latitude DECIMAL(10, 7),
    longitude DECIMAL(10, 7),
    pickup_time_note VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_points_operator FOREIGN KEY (operator_id) REFERENCES bus_operators(id),
    CONSTRAINT fk_points_route FOREIGN KEY (route_id) REFERENCES routes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: trips
-- =====================================================
CREATE TABLE IF NOT EXISTS trips (
    id INT AUTO_INCREMENT PRIMARY KEY,
    operator_id INT NOT NULL,
    route_id INT NOT NULL,
    vehicle_type_id INT NOT NULL,
    departure_date DATE NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME,
    price DECIMAL(12, 0) NOT NULL,
    available_seats INT NOT NULL,
    total_seats INT NOT NULL,
    status ENUM('active', 'full', 'cancelled', 'completed') NOT NULL DEFAULT 'active',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trips_operator FOREIGN KEY (operator_id) REFERENCES bus_operators(id),
    CONSTRAINT fk_trips_route FOREIGN KEY (route_id) REFERENCES routes(id),
    CONSTRAINT fk_trips_vehicle_type FOREIGN KEY (vehicle_type_id) REFERENCES vehicle_types(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: seats
-- =====================================================
CREATE TABLE IF NOT EXISTS seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    trip_id INT NOT NULL,
    seat_code VARCHAR(10) NOT NULL,
    floor INT NOT NULL DEFAULT 1,
    row_num INT NOT NULL,
    col_num INT NOT NULL,
    status ENUM('available', 'booked', 'locked') NOT NULL DEFAULT 'available',
    locked_by INT,
    CONSTRAINT fk_seats_trip FOREIGN KEY (trip_id) REFERENCES trips(id),
    CONSTRAINT uk_seats_trip_code UNIQUE (trip_id, seat_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: bookings
-- =====================================================
CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_code VARCHAR(20) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    trip_id INT NOT NULL,
    seat_id INT NOT NULL,
    pickup_point_id INT,
    dropoff_point_id INT,
    passenger_name VARCHAR(100) NOT NULL,
    passenger_phone VARCHAR(15) NOT NULL,
    passenger_email VARCHAR(100),
    ticket_price DECIMAL(12, 0) NOT NULL,
    payment_method ENUM('cod', 'momo', 'zalopay', 'bank_transfer') NOT NULL DEFAULT 'cod',
    payment_status ENUM('pending', 'paid', 'refunded') NOT NULL DEFAULT 'pending',
    booking_status ENUM('pending', 'confirmed', 'completed', 'cancelled') NOT NULL DEFAULT 'pending',
    ticket_type ENUM('one_way', 'round_trip') NOT NULL DEFAULT 'one_way',
    cancel_deadline DATETIME,
    cancelled_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_trip FOREIGN KEY (trip_id) REFERENCES trips(id),
    CONSTRAINT fk_bookings_seat FOREIGN KEY (seat_id) REFERENCES seats(id),
    CONSTRAINT fk_bookings_pickup_point FOREIGN KEY (pickup_point_id) REFERENCES pickup_dropoff_points(id),
    CONSTRAINT fk_bookings_dropoff_point FOREIGN KEY (dropoff_point_id) REFERENCES pickup_dropoff_points(id),
    CONSTRAINT uk_bookings_code UNIQUE (booking_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: reviews
-- =====================================================
CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    trip_id INT NOT NULL,
    operator_id INT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    is_verified BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_trip FOREIGN KEY (trip_id) REFERENCES trips(id),
    CONSTRAINT fk_reviews_operator FOREIGN KEY (operator_id) REFERENCES bus_operators(id),
    CONSTRAINT uk_reviews_user_trip UNIQUE (user_id, trip_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Indexes for performance
-- =====================================================
CREATE INDEX idx_trips_date ON trips(departure_date);
CREATE INDEX idx_trips_route ON trips(route_id);
CREATE INDEX idx_trips_operator ON trips(operator_id);
CREATE INDEX idx_trips_status ON trips(status);
CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_trip ON bookings(trip_id);
CREATE INDEX idx_bookings_code ON bookings(booking_code);
CREATE INDEX idx_bookings_status ON bookings(booking_status);
CREATE INDEX idx_seats_trip ON seats(trip_id);
CREATE INDEX idx_seats_status ON seats(status);
CREATE INDEX idx_routes_departure ON routes(departure_province_id);
CREATE INDEX idx_routes_destination ON routes(destination_province_id);
CREATE INDEX idx_reviews_operator ON reviews(operator_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);
