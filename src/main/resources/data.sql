-- SQL Data Seed for EduSmart application
-- Passwords are all BCrypt-encrypted version of "password123": $2a$10$mY8xN741ifT1R28mD9VbieEvotbVPhKjgt0uXz4mTZRkovw5OQI9.

-- Seed Admin User
MERGE INTO users (id, username, password, email, role, created_at, updated_at) KEY(username)
VALUES (1, 'admin', '$2a$10$mY8xN741ifT1R28mD9VbieEvotbVPhKjgt0uXz4mTZRkovw5OQI9.', 'admin@edusmart.com', 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Instructor User
MERGE INTO users (id, username, password, email, role, created_at, updated_at) KEY(username)
VALUES (2, 'instructor', '$2a$10$mY8xN741ifT1R28mD9VbieEvotbVPhKjgt0uXz4mTZRkovw5OQI9.', 'instructor@edusmart.com', 'ROLE_INSTRUCTOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Student User
MERGE INTO users (id, username, password, email, role, created_at, updated_at) KEY(username)
VALUES (3, 'student', '$2a$10$mY8xN741ifT1R28mD9VbieEvotbVPhKjgt0uXz4mTZRkovw5OQI9.', 'student@edusmart.com', 'ROLE_STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Course
MERGE INTO courses (id, title, description, category, instructor_id, status, created_at, updated_at) KEY(id)
VALUES (1, 'Mengelola Data untuk Analisis Trend', 'Pelajari konsep struktur modal dan diversifikasi pendapatan untuk keunggulan finansial korporat.', 'Data Science', 2, 'PUBLISHED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Enrollment
MERGE INTO enrollments (id, user_id, course_id, progress_percent, created_at, updated_at) KEY(user_id, course_id)
VALUES (1, 3, 1, 45, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
