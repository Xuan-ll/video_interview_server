package io.openvidu.basic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.openvidu.basic.entity.Interview;
import java.util.List;

public interface InterviewService extends IService<Interview> {

    String getToken(String roomName, String userName) throws Exception;
    String createInterview(String roomName, List<String> participants, List<String> interviewers, long scheduledTime, String hrName, long createdAt);
    // 如果还有其他方法，可以继续添加
}