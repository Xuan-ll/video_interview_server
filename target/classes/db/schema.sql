CREATE DATABASE IF NOT EXISTS video_interview DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE video_interview;

CREATE TABLE IF NOT EXISTS interview (
    room_id VARCHAR(64) PRIMARY KEY COMMENT '房间ID',
    room_name VARCHAR(255) NOT NULL COMMENT '房间名称',
    scheduled_time DATETIME NOT NULL COMMENT '预定时间',
    participants TEXT NOT NULL COMMENT '参与者列表，JSON格式',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待开始，1-进行中，2-已结束，3-已取消',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    version INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    INDEX idx_room_name (room_name),
    INDEX idx_scheduled_time (scheduled_time),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='面试会话表';
