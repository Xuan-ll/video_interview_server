package io.openvidu.basic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import io.openvidu.basic.service.GetInterviewsResp;
import io.openvidu.basic.config.WebConfig;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview> implements InterviewService {

    @Value("${livekit.api.key}")
    private String LIVEKIT_API_KEY;

    @Value("${livekit.api.secret}")
    private String LIVEKIT_API_SECRET;

    private final ObjectMapper objectMapper;

    private final InterviewMapper interviewMapper;

    private final RestTemplate restTemplate;

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
    public Map<String, String> createInterview(String roomName, List<String> participants, List<String> interviewers, long scheduledTime, String hrName,String position, String period, long createdAt) {
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
            interview.setScheduledTime(scheduledTimeL);
            interview.setParticipants(objectMapper.writeValueAsString(participants));
            interview.setInterviewers(objectMapper.writeValueAsString(interviewers));
            interview.setHrName(hrName);
            interview.setCreatedAt(createdAtL);
            interview.setUpdatedAt(updatedAtL);
            interview.setPosition(position);
            interview.setInterviewPeriod(period);
            interview.setInterviewStatus(0);
            interview.setInterviewText("");           
            Random random = new Random();
            int roomPassword = random.nextInt(900000) + 100000; // 生成 6 位随机数
            String roomPasswordString = String.valueOf(roomPassword);
            interview.setInterviewPassword(roomPasswordString); // 转换为字符串
            boolean isOK = save(interview);

            if (isOK) {
                Map<String, String> result = new HashMap<>();
                result.put("roomId", interview.getRoomId());
                result.put("roomPassword", roomPasswordString);
                return result;
            } else {
                return null;
            }
        } catch (JsonProcessingException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public List<GetInterviewsResp> getInterviewsByHrName(String hrName) {
        QueryWrapper<Interview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("hr_name", hrName);
        List<Interview> interviews = interviewMapper.selectList(queryWrapper);
        List<GetInterviewsResp> result = new ArrayList<>();
        for (Interview interview : interviews) {
            result.add(new GetInterviewsResp(interview));
        }        
        return result;
    }  
    
    @Override
    public GetInterviewsResp getInterviewsByRoomId(String roomId) {
        QueryWrapper<Interview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId);
        Interview interview = interviewMapper.selectOne(queryWrapper);
        if (interview == null) {
            return null;
        }
        return new GetInterviewsResp(interview);
    }
    @Override
    public String updateInterview(String roomId, String roomName, List<String> participants, List<String> interviewers, long scheduledTime, String hrName, String position, String period, long updatedAt){
        try {
            // 根据roomId查询现有记录
            QueryWrapper<Interview> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("room_id", roomId);
            Interview interview = interviewMapper.selectOne(queryWrapper);
    
            if (interview == null) {
                return "查询的房间不存在";
            }
            if (interview.getInterviewStatus() != 0) {
                return "面试已经开始，无法修改";
            }
    
            // 更新字段（非空判断需要根据业务需求调整）
            LocalDateTime scheduledTimeL = Instant.ofEpochMilli(scheduledTime)
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDateTime();
            LocalDateTime updatedAtL = Instant.ofEpochMilli(updatedAt)
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDateTime();
    
            // 只更新传入非空参数
            if (participants != null) {
                interview.setParticipants(objectMapper.writeValueAsString(participants));
            }
            if (interviewers != null) {
                interview.setInterviewers(objectMapper.writeValueAsString(interviewers));
            }
            interview.setRoomName(roomName);
            interview.setScheduledTime(scheduledTimeL);
            interview.setHrName(hrName);
            interview.setPosition(position);
            interview.setInterviewPeriod(period);
            interview.setUpdatedAt(updatedAtL);
            int isOK = interviewMapper.updateById(interview);
            if (isOK==1) {
                return "";
            } else {
                return "更新面试信息失败";
            }
        } catch (Exception e) {
            return "更新面试信息失败";
        }
    }
    @Override
    public  String checkRoomPassword(String roomId, String roomPassword, Long nowTime) {
        try {
            QueryWrapper<Interview> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("room_id", roomId);
            Interview interview = interviewMapper.selectOne(queryWrapper);
            if (interview == null) {
                return "会议不存在";
            } 
            if (interview.getInterviewStatus()== 2) {
                return "会议已结束";
            }
            if (interview.getInterviewStatus() == 3) {
                return "会议已取消";
            }
            if (interview.getInterviewPassword().equals(roomPassword)) {
                // 检查时间是否过期
                LocalDateTime nowTimeL = Instant.ofEpochMilli(nowTime)
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDateTime();
                LocalDateTime availableTime = interview.getScheduledTime().minusMinutes(5);
                if (nowTimeL.isBefore(availableTime)) {
                    return "会议还未开始";
                }
                return interview.getRoomName();
            } else {
                return "密码错误";
            }
        } catch (Exception e) {
            return "查询会议失败";
        }
    }
    @Override
    public String endInterview(String roomId, int status) {
        try {
            QueryWrapper<Interview> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("room_id", roomId);
            Interview interview = interviewMapper.selectOne(queryWrapper);
            if (interview == null) {
                return "会议不存在";
            }
            interview.setInterviewStatus(status);
            int isOK = interviewMapper.updateById(interview);
            if (isOK==1) {
                return "";
            } else {
                return "结束会议失败";
            }
        } catch (Exception e) {
            return "结束会议失败";
        }
    }
    @Override
    public String saveNote(String roomId,String snote) {
        try {
            QueryWrapper<Interview> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("room_id", roomId);
            Interview interview = interviewMapper.selectOne(queryWrapper);
            if (interview == null) {
                return "会议不存在";
            }
            interview.setInterviewText(snote);
            int isOK = interviewMapper.updateById(interview);
            if (isOK==1) {
                return "";
            } else {
                return "保存笔记失败";
            }
        } catch (Exception e) {
            return "保存笔记失败";  
        }
    }

    @Override
    public String getNote(String roomId) {
        try {
            QueryWrapper<Interview> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("room_id", roomId);
            Interview interview = interviewMapper.selectOne(queryWrapper);
            if (interview == null) {
                return "";
            }
            return interview.getInterviewText();
        } catch (Exception e) {
            return "";
        }  
    }
    @Override
    public Map<String, Object> getUserInfoByToken(String authUrl, String token) {
        try {
            // 构建带有token参数的URL
            String url = UriComponentsBuilder.fromHttpUrl(authUrl)
                    .queryParam("token", token)
                    .toUriString();
            
            // 发送GET请求
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            // 检查响应状态
            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> responseBody = response.getBody();
                
                // 处理返回的用户信息
                Map<String, Object> processedUserInfo = new HashMap<>();
                
                // 假设返回的数据结构包含code和data字段
                if (responseBody != null && responseBody.containsKey("code") && (Integer)responseBody.get("code") == 200) {
                    Map<String, Object> userData = (Map<String, Object>) responseBody.get("data");
                    
                    // 提取需要的用户信息
                    if (userData != null) {
                        processedUserInfo.put("userId", userData.get("id"));
                        processedUserInfo.put("username", userData.get("username"));
                        processedUserInfo.put("nickname", userData.get("nickname"));
                        processedUserInfo.put("role", userData.get("role"));
                        // 可以根据实际需要提取更多字段
                        return processedUserInfo;
                    }
                    else {
                        processedUserInfo.put("error", "用户信息查询为空");
                        return processedUserInfo;
                    }
                } else {
                    // 鉴权失败
                    processedUserInfo.put("error", "鉴权失败，未找到该用户信息");
                    return processedUserInfo;
                }
            } else {
                // 请求失败
                Map<String, Object> errorInfo = new HashMap<>();
                errorInfo.put("error", "Request failed with status: " + response.getStatusCodeValue());
                return errorInfo;
            }
        } catch (Exception e) {
            // 异常处理
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", "Failed to authenticate user: " + e.getMessage());
            return errorInfo;
        }
    }
}