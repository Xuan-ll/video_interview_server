package io.openvidu.basic.service;

import java.util.List;
import java.time.LocalDateTime;
import io.openvidu.basic.entity.Interview;

public class GetInterviewsResp {
    public String roomId;
    public String roomName;
    public LocalDateTime scheduledTime;
    public String participants;
    public String interviewers;
    public Integer interviewStatus;
    public String position;
    public String interviewPeriod;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public GetInterviewsResp(Interview interview) {
        this.roomId = interview.getRoomId();
        this.roomName = interview.getRoomName();
        this.scheduledTime = interview.getScheduledTime();
        this.participants = interview.getParticipants();
        this.interviewers = interview.getInterviewers();
        this.interviewStatus = interview.getInterviewStatus();
        this.position = interview.getPosition();
        this.interviewPeriod = interview.getInterviewPeriod();
        this.createdAt = interview.getCreatedAt();
        this.updatedAt = interview.getUpdatedAt();
    }
}