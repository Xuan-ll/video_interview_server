package io.openvidu.basic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.openvidu.basic.entity.Interview;
import io.openvidu.basic.mapper.InterviewMapper;
import io.openvidu.basic.service.InterviewService;
import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview> implements InterviewService {

    @Value("${livekit.api.key}")
    private String LIVEKIT_API_KEY;

    @Value("${livekit.api.secret}")
    private String LIVEKIT_API_SECRET;

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
    @Transactional
    public boolean createInterview(String roomName, List<String> participants, long scheduledTime, long createdAt) {
        try {
            LocalDateTime scheduledTimeL = Instant.ofEpochMilli(scheduledTime)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime createdAtL = Instant.ofEpochMilli(createdAt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime updatedAtL = LocalDateTime.now(); 

            Interview interview = new Interview();
            interview.setRoomName(roomName);
            interview.setParticipants(objectMapper.writeValueAsString(participants));
            interview.setScheduledTime(scheduledTimeL);
            interview.setCreatedAt(createdAtL);
            interview.setUpdatedAt(updatedAtL);

            return save(interview);
        } catch (JsonProcessingException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}