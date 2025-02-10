package io.openvidu.basic.controller;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import io.openvidu.basic.service.InterviewService;

@CrossOrigin(origins = "*")
@RestController
public class InterviewController {
    @Autowired
	private InterviewService interviewService;

	/**
	 * @param params JSON object with roomName and participantName and scheduledTime and createdAt
	 * @return ok
	 */    
    @PostMapping(value = "/createSession")
    public ResponseEntity<Map<String, String>> createSession(@RequestBody Map<String, String> params) {
    	String roomName = params.get("roomName");
		String participantList = params.get("participantName");
		List<String> participantName = new ArrayList<>();
        if (participantList != null) {
            participantName = Arrays.asList(participantList.split(","));
        }
		Long scheduledTime = Long.parseLong(params.get("scheduledTime"));
		Long createdAt = Long.parseLong(params.get("createdAt"));
		boolean isOK = false;
		isOK = interviewService.createInterview(roomName, participantName, scheduledTime, createdAt);
        try {
            isOK = interviewService.createInterview(roomName, participantName, scheduledTime, createdAt);
        } catch (Exception e) {
            // 记录异常日志（如果需要）
            e.printStackTrace();
        }

        // 根据 isOK 返回不同的响应
        if (isOK) {
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "roomName", roomName,
                "participantName", String.join(",", participantName),
                "scheduledTime", String.valueOf(scheduledTime),
                "createdAt", String.valueOf(createdAt)
            ));
        } else {
            return ResponseEntity.status(400).body(Map.of(
                "errorMessage", "Failed to create session"
            ));
        }
    }
	/**
	 * @param params JSON object with roomName and participantName
	 * @return JSON object with the JWT token
	 */
	 @PostMapping(value = "/getToken")
	 public ResponseEntity<Map<String, String>> createToken(@RequestBody Map<String, String> params) {
        String roomName = params.get("roomName");
        String participantName = params.get("participantName");

        // 参数校验
        if (roomName == null || participantName == null) {
            return ResponseEntity.badRequest().body(Map.of("errorMessage", "roomName and participantName are required"));
        }

        try {
            // 调用 getToken 方法生成 Token
            String token = interviewService.getToken(roomName, participantName);

            // 返回成功响应
            return ResponseEntity.ok(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            // 参数错误
            return ResponseEntity.badRequest().body(Map.of("errorMessage", e.getMessage()));
        } catch (Exception e) {
            // 其他异常，例如 Token 生成失败
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errorMessage", "Failed to generate token"));
        }
    }









	// @Value("${livekit.api.key}")
	// private String LIVEKIT_API_KEY;

	// @Value("${livekit.api.secret}")
	// private String LIVEKIT_API_SECRET;

	// /**
	//  * @param params JSON object with roomName and participantName
	//  * @return JSON object with the JWT token
	//  */
	//  @PostMapping(value = "/token")
	// public ResponseEntity<Map<String, String>> createToken(@RequestBody Map<String, String> params) {
	// 	String roomName = params.get("roomName");
	// 	String participantName = params.get("participantName");

	// 	if (roomName == null || participantName == null) {
	// 		return ResponseEntity.badRequest().body(Map.of("errorMessage", "roomName and participantName are required"));
	// 	}

	// 	AccessToken token = new AccessToken(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
	// 	token.setName(participantName);
	// 	token.setIdentity(participantName);
	// 	token.addGrants(new RoomJoin(true), new RoomName(roomName));

	// 	return ResponseEntity.ok(Map.of("token", token.toJwt()));
	// }

	// @PostMapping(value = "/livekit/webhook", consumes = "application/webhook+json")
	// public ResponseEntity<String> receiveWebhook(@RequestHeader("Authorization") String authHeader, @RequestBody String body) {
	// 	WebhookReceiver webhookReceiver = new WebhookReceiver(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
	// 	try {
	// 		WebhookEvent event = webhookReceiver.receive(body, authHeader);
	// 		System.out.println("LiveKit Webhook: " + event.toString());
	// 	} catch (Exception e) {
	// 		System.err.println("Error validating webhook event: " + e.getMessage());
	// 	}
	// 	return ResponseEntity.ok("ok");
	// }

}
