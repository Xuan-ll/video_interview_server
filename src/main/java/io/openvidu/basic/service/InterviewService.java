package io.openvidu.basic.service;

import io.openvidu.basic.entity;

import java.io.IOException;
import java.util.List;

/**
 * 面试服务接口
 */
public interface InterviewService {

    /**
     * 用户加入面试
     *
     * @param roomName     房间名称 
     * @param userName     用户名称
     * @return 加入面试的 Token
     * @throws Exception 异常
     */
    String getToken(String roomName, String userName) throws Exception;
    boolean createInterview(String roomName, List<String> participants, long scheduledTime, long createdAt);
    // 如果还有其他方法，可以继续添加
}