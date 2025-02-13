package io.openvidu.basic.controller;

import java.util.Map;
import java.util.List;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.openvidu.basic.service.InterviewService;

@CrossOrigin(origins = "*")
@RestController
public class InterviewController {
    @Autowired
    private InterviewService interviewService;

    /**
     * 创建面试会话
     * @param params 包含 roomName, participantName, scheduledTime 的参数
     * @return 创建结果
     */    
    @PostMapping("/create")
    public ResponseEntity<?> createSession(@RequestBody Map<String, String> params) {
        try {
            String roomName = params.get("roomName");
            String participantList = params.get("participantName");
            if (roomName == null || participantList == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "房间名和参与者列表不能为空"));
            }

            List<String> participantNames = Arrays.asList(participantList.split(","));
            Long scheduledTime = Long.parseLong(params.get("scheduledTime"));
            Long createdAt = System.currentTimeMillis();

            boolean success = interviewService.createInterview(roomName, participantNames, scheduledTime, createdAt);
            
            if (success) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "面试会话创建成功",
                    "data", Map.of(
                        "roomName", roomName,
                        "participants", participantNames,
                        "scheduledTime", scheduledTime
                    )
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "面试会话创建失败"));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("status", "error", "message", "时间格式不正确"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "服务器内部错误"));
        }
    }

    /**
     * 获取面试Token
     * @param params 包含 roomName 和 userName 的参数
     * @return 面试Token
     */
    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody Map<String, String> params) {
        try {
            String roomName = params.get("roomName");
            String userName = params.get("userName");
            
            if (roomName == null || userName == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "房间名和用户名不能为空"));
            }

            String token = interviewService.getToken(roomName, userName);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", Map.of("token", token)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * Simple endpoint to test POST request reception
     * @param params JSON object to be echoed back
     * @return Echoed JSON object
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> testPostReception(@RequestBody Map<String, String> params) {
        // Log the received parameters
        System.out.println("Received parameters: " + params);
        // Echo back the received parameters
        return ResponseEntity.ok(params);
    }
}
