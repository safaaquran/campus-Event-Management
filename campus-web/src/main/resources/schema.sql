CREATE DATABASE IF NOT EXISTS campus_event_system;
USE campus_event_system;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    faculty VARCHAR(120) NOT NULL,
    department VARCHAR(120) NOT NULL,
    admission_year INT NOT NULL,
    role VARCHAR(20) NOT NULL,
    blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS departments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    organizer_id BIGINT NOT NULL,
    organizer_name VARCHAR(120) NOT NULL,
    description TEXT NOT NULL,
    department_club VARCHAR(120) NOT NULL,
    event_datetime TIMESTAMP NOT NULL,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL,
    seats_remaining INT NOT NULL,
    category VARCHAR(30) NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    event_image VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organizer_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    reservation_status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    attendance_status VARCHAR(20) NOT NULL DEFAULT 'UNMARKED',
    reserved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_event_student (event_id, student_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS event_ratings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    feedback VARCHAR(500),
    rated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_event_rating_student (event_id, student_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT IGNORE INTO users (id, full_name, email, password_hash, faculty, department, admission_year, role, blocked)
VALUES (1, 'System Admin', 'admin@campus.edu', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administration', 'IT', 2020, 'ADMIN', FALSE);

INSERT IGNORE INTO departments(name) VALUES ('Computer Science'), ('Electrical Engineering'), ('Business'), ('Media');
INSERT IGNORE INTO categories(name) VALUES ('Educational'), ('Social'), ('Sports'), ('Cultural'), ('Technical');
