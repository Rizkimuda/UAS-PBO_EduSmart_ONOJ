-- SQL Data Seed for EduSmart application
-- Passwords are all BCrypt-encrypted version of "password123": $2a$10$8.2luT9f.68er8v4FAoVCOY3y6rUdf7tP3tW/WbX8/v.r8aHk.H3G

-- Seed Admin User
INSERT INTO users (username, password, email, role, created_at, updated_at)
VALUES ('admin', '$2a$10$8.2luT9f.68er8v4FAoVCOY3y6rUdf7tP3tW/WbX8/v.r8aHk.H3G', 'admin@edusmart.com', 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Instructor User
INSERT INTO users (username, password, email, role, created_at, updated_at)
VALUES ('instructor', '$2a$10$8.2luT9f.68er8v4FAoVCOY3y6rUdf7tP3tW/WbX8/v.r8aHk.H3G', 'instructor@edusmart.com', 'ROLE_INSTRUCTOR', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Student User
INSERT INTO users (username, password, email, role, created_at, updated_at)
VALUES ('student', '$2a$10$8.2luT9f.68er8v4FAoVCOY3y6rUdf7tP3tW/WbX8/v.r8aHk.H3G', 'student@edusmart.com', 'ROLE_STUDENT', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
