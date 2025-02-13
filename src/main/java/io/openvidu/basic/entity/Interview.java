package io.openvidu.basic.entity;

import com.baomidou.mybatisplus.annotation.*;
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
    
    @TableField("status")
    private Integer status; // 0: 待开始, 1: 进行中, 2: 已结束, 3: 已取消
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

}