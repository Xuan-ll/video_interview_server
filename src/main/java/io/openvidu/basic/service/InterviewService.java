package io.openvidu.basic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.openvidu.basic.entity.Interview;
import java.util.List;
import java.util.Map;

public interface InterviewService extends IService<Interview> {
    String getToken(String roomName, String userName) throws Exception;
    Map<String, String> createInterview(String roomName, List<String> participants, List<String> interviewers, long scheduledTime, String hrName,String position, String period, long createdAt);
    String updateInterview(String roomId, String roomName, List<String> participants, List<String> interviewers, long scheduledTime, String hrName, String position, String period, long updatedAt);
    List<GetInterviewsResp> getInterviewsByHrName(String hrName);
    GetInterviewsResp getInterviewsByRoomId(String roomId);
    String  checkRoomPassword(String roomId, String roomPassword, Long nowTime);
    String endInterview(String roomId, int status);  
    String saveNote(String roomId,String snote);
    String getNote(String roomId);
    Map<String, Object> Map<String, Object> getUserInfoByToken(String authUrl, String token);
}