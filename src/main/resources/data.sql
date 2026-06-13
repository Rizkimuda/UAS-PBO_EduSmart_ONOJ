-- SQL Data Seed for EduSmart application
-- Passwords are all BCrypt-encrypted version of "password123": $2a$10$mY8xN741ifT1R28mD9VbieEvotbVPhKjgt0uXz4mTZRkovw5OQI9.

-- Seed Admin User
MERGE INTO users (username, password, email, role, created_at, updated_at) KEY(username)
VALUES ('admin', '$2a$10$mY8xN741ifT1R28mD9VbieEvotbVPhKjgt0uXz4mTZRkovw5OQI9.', 'admin@edusmart.com', 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Instructor User
MERGE INTO users (username, password, email, role, created_at, updated_at) KEY(username)
VALUES ('instructor', '$2a$10$mY8xN741ifT1R28mD9VbieEvotbVPhKjgt0uXz4mTZRkovw5OQI9.', 'instructor@edusmart.com', 'ROLE_INSTRUCTOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Student User
MERGE INTO users (username, password, email, role, created_at, updated_at) KEY(username)
VALUES ('student', '$2a$10$mY8xN741ifT1R28mD9VbieEvotbVPhKjgt0uXz4mTZRkovw5OQI9.', 'student@edusmart.com', 'ROLE_STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
