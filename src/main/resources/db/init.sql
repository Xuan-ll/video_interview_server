-- 创建数据库
CREATE DATABASE IF NOT EXISTS video_interview;
USE video_interview;

-- 创建面试表
CREATE TABLE IF NOT EXISTS interview (
    room_id VARCHAR(64) PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL,
    scheduled_time DATETIME NOT NULL,
    participants TEXT NOT NULL,
    created_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
