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

-- Reset H2 Auto-increment/Identity sequences to avoid key conflicts
ALTER TABLE users ALTER COLUMN id RESTART WITH 10;
ALTER TABLE courses ALTER COLUMN id RESTART WITH 10;

-- Seed Materials for Course 1
MERGE INTO materials (id, title, type, content, order_index, course_id, created_at, updated_at) KEY(id)
VALUES (1, 'Pendahuluan Analisis Trend', 'TEXT', 'Analisis trend adalah teknik analisis statistik yang digunakan untuk meramalkan arah pergerakan data di masa depan berdasarkan pola historis. Analisis ini sangat penting dalam berbagai bidang seperti keuangan, pemasaran, dan ilmu data untuk membantu pengambilan keputusan yang lebih akurat.', 1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO materials (id, title, type, content, order_index, course_id, created_at, updated_at) KEY(id)
VALUES (2, 'Metode Garis Linear (Least Square)', 'TEXT', 'Metode least square atau kuadrat terkecil adalah salah satu metode analisis trend yang paling populer. Formula matematis untuk trend linear adalah Y = a + bX. Di mana a adalah konstanta intersep dan b adalah kemiringan garis (slope). Metode ini meminimalkan jumlah kuadrat deviasi antara data aktual dan garis trend.', 2, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO materials (id, title, type, content, order_index, course_id, created_at, updated_at) KEY(id)
VALUES (3, 'Visualisasi Data Trend', 'TEXT', 'Visualisasi trend mempermudah pemangku kepentingan memahami pergerakan data. Grafik garis (line chart) adalah visualisasi terbaik untuk menampilkan data deret waktu (time series). Menambahkan garis trend (trendline) membantu mendeteksi apakah data cenderung naik (uptrend) atau turun (downtrend).', 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE materials ALTER COLUMN id RESTART WITH 10;

-- Seed Quiz for Course 1
MERGE INTO quizzes (id, title, time_limit, passing_score, max_attempts, course_id, created_at, updated_at) KEY(id)
VALUES (1, 'Kuis Evaluasi Analisis Trend', 15, 70, 3, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE quizzes ALTER COLUMN id RESTART WITH 10;

-- Seed Questions for Quiz 1
MERGE INTO questions (id, q_type, question_text, points, correct_answer, explanation, quiz_id, option_a, option_b, option_c, option_d, created_at, updated_at) KEY(id)
VALUES (1, 'MULTIPLE_CHOICE', 'Apa formula garis linear untuk analisis trend menggunakan metode least square?', 50, 'A', 'Formula trend linear adalah Y = a + bX, di mana a adalah konstanta intersep dan b adalah slope.', 1, 'Y = a + bX', 'Y = aX^2 + bX + c', 'Y = a * b^X', 'Y = a - bX', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

MERGE INTO questions (id, q_type, question_text, points, correct_answer, explanation, quiz_id, created_at, updated_at) KEY(id)
VALUES (2, 'ESSAY', 'Sebutkan grafik/chart terbaik untuk menampilkan data deret waktu (time series) dalam visualisasi trend! (Kunci: garis)', 50, 'garis', 'Grafik garis (line chart) adalah jenis grafik terbaik dan paling umum digunakan untuk memvisualisasikan data time series.', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE questions ALTER COLUMN id RESTART WITH 10;


