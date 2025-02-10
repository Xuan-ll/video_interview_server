package io.openvidu.basic.service.impl;

// 基础包
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

// Lombok 注解
import lombok.RequiredArgsConstructor;

// Spring 注解
import org.springframework.stereotype.Service;

// Jackson JSON 处理
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;

// 自定义类
import io.openvidu.basic.entity.Interview;
import io.openvidu.basic.mapper.InterviewMapper;
import io.openvidu.basic.service.InterviewService;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.livekit.server.WebhookReceiver;
import livekit.LivekitWebhook.WebhookEvent;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {
	@Value("${livekit.api.key}")
	private String LIVEKIT_API_KEY;

	@Value("${livekit.api.secret}")
	private String LIVEKIT_API_SECRET;

	private final InterviewMapper interviewMapper;
    private final ObjectMapper objectMapper;

    @Override
    public String getToken(String roomName, String userName) throws Exception {
		if (roomName == null || userName == null) {
			throw new IllegalArgumentException("Room name or user name cannot be null");
		}
        try {
			AccessToken token = new AccessToken(LIVEKIT_API_KEY, LIVEKIT_API_SECRET);
			token.setName(userName);
			token.setIdentity(userName);
			token.addGrants(new RoomJoin(true), new RoomName(roomName));

			return token.toJwt();
		} catch (Exception e) {
			throw new Exception("Failed to generate token", e);
        }
    }
	@Override
	public boolean createInterview(String roomName, List<String> participants, long scheduledTime, long createdAt) {
		// LocalDateTime scheduledTimeL = LocalDateTime.ofInstant(
		// 	Instant.ofEpochMilli(scheduledTime), 
		// 	ZoneId.systemDefault()
		// );
		// LocalDateTime createdAtL = LocalDateTime.ofInstant(
		// 	Instant.ofEpochMilli(createdAt), 
		// 	ZoneId.systemDefault()
		// );
		LocalDateTime scheduledTimeL = Instant.ofEpochMilli(scheduledTime)
                                  .atZone(ZoneId.systemDefault())
                                  .toLocalDateTime();
		LocalDateTime createdAtL = Instant.ofEpochMilli(createdAt)
                                  .atZone(ZoneId.systemDefault())
                                  .toLocalDateTime();
		Interview interview = new Interview();
		interview.setRoomName(roomName);
		interview.setParticipants(objectMapper.writeValueAsString(participants));
		interview.setScheduledTime(scheduledTime);
		interview.setCreatedAt(createdAt);
		return interviewMapper.insert(interview) > 0;
	}
}