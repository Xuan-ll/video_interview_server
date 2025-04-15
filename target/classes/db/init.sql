-- 创建数据库
CREATE DATABASE IF NOT EXISTS video_interview;
USE video_interview;

-- 创建面试表
CREATE TABLE IF NOT EXISTS interview (
    room_id VARCHAR(64) PRIMARY KEY,
    room_name VARCHAR(255) NOT NULL,
    scheduled_time DATETIME NOT NULL,
    participants TEXT NOT NULL,
    interviewers TEXT NOT NULL,
    hr_name    VARCHAR(255) NOT NULL,
    interview_status     INT NOT NULL DEFAULT 0,
    position   VARCHAR(255) NOT NULL,
    interview_period     VARCHAR(255) NOT NULL, 
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    interview_password   VARCHAR(255) NOT NULL,
    interview_text TEXT 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
