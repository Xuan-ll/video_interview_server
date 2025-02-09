package io.openvidu.basic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("interview")
public class Interview {
    @TableId(type = IdType.ASSIGN_UUID)
    private String roomId;

    @TableField("room_name")
    private String roomName;
    
    @TableField("scheduled_time")
    private LocalDateTime scheduledTime;
    
    @TableField("participants")
    private String participants; // 存储为JSON字符串，如 ["hr1", "candidate1"]
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}