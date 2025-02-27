package io.openvidu.basic.controller;

import java.util.Map;
import java.util.List;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.openvidu.basic.service.InterviewService;
import io.openvidu.basic.service.GetInterviewsResp;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/room")  
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
            String participantList = params.get("candList");
            String interviewerList = params.get("hrList");
            Long scheduledTime = Long.parseLong(params.get("time"));
            if (roomName == null || participantList == null || interviewerList == null || scheduledTime == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "房间名、参与者列表、预约时间不能为空"));
            }
            List<String> participantNames = Arrays.asList(participantList.split(","));
            List<String> interviewerNames = Arrays.asList(interviewerList.split(","));
            String hrName = params.get("hrName");
            String position = params.get("position");
            String period = params.get("period");
            Long createdAt = System.currentTimeMillis();

            Map<String, String> roomInfo = interviewService.createInterview(roomName, participantNames, interviewerNames, scheduledTime, hrName, position ,period, createdAt);
            if (roomInfo != null) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "面试会话创建成功",
                    "data", Map.of(
                        "roomPwd", roomInfo.get("roomPassword"),
                        "roomId", roomInfo.get("roomId")
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
            String userName = params.get("participantName");
            
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
     * hr获取所有预约的房间
     * @param hrName hr名字
     * @return 所有预约的房间
     */
    @GetMapping("/get_all_rooms")
    public ResponseEntity<?> getInterviewsByHrName(@RequestParam String hrName) {
        try {
            List<GetInterviewsResp> interviews = interviewService.getInterviewsByHrName(hrName);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", interviews
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    /**
     * 获取房间信息
     * @param roomId 房间ID
     * @return 房间信息
     */
    @GetMapping("/get_room/{roomId}")
    public ResponseEntity<?> getInterviewsByRoomId(@PathVariable String roomId) {
        try {
            GetInterviewsResp interview = interviewService.getInterviewsByRoomId(roomId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", interview
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }


    /**
     * 修改已经创建了的会议
     * @param params 包含 roomName（必填）及其他可修改参数
     * @return 修改结果
     */
    @PutMapping("/update/{roomId}")
    public ResponseEntity<?> updateSession( @PathVariable String roomId, @RequestBody Map<String, String> params) {
        try {
            String roomName = params.get("roomName");
            if (roomName == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "房间名不能为空"));
            }       
            // 可修改参数
            List<String> participantNames = null;
            List<String> interviewerNames = null;
            Long scheduledTime = null;
            participantNames = Arrays.asList(params.get("candList").split(","));
            interviewerNames = Arrays.asList(params.get("hrList").split(","));
            scheduledTime = Long.parseLong(params.get("time"));
            String position = params.get("position");
            String period = params.get("period");
            String hrName = params.get("hrName");
            Long updateAt = System.currentTimeMillis();
            
            String msg = interviewService.updateInterview(
                roomId,
                roomName,
                participantNames,
                interviewerNames,
                scheduledTime,
                hrName,
                position,
                period,
                updateAt
            );
            
            if (msg.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "面试会话修改成功"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", msg));
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
     * 加入会议
     * @param params 包含 roomId, roomPwd, time 的参数
     * @return 加入结果
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinSession(@RequestBody Map<String, String> params) {
        try {
            String roomId = params.get("roomId");
            String roomPassword = params.get("roomPwd");
            Long nowTime = Long.parseLong(params.get("time"));
            if (roomId == null || roomPassword == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "房间ID和密码不能为空"));
            }
            String roomName = interviewService.checkRoomPassword(roomId, roomPassword, nowTime);
            if (roomName == "会议不存在" || roomName == "密码错误" || roomName == "会议还未开始" || roomName == "会议已结束" || roomName == "会议已取消" || roomName == "查询会议失败") {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(Map.of("status", "error", "message", roomName));
            }
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "成功加入房间",
                "data", Map.of("roomName", roomName)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    /**
     * 结束视频会议
     * @param params 包含 roomId、status 的参数
     * @return 取消结果
     */
    @PostMapping("/end")
    public ResponseEntity<?> endSession(@RequestBody Map<String, String> params) {
        try {
            String roomId = params.get("roomId");
            Integer status = Integer.parseInt(params.get("status"));
            if (roomId == null || status == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "房间ID和状态不能为空"));
            }
            String msg = interviewService.endInterview(roomId, status);
            if (msg.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "面试会话取消成功"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", msg));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("status", "error", "message", "状态格式不正确"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "服务器内部错误"));
        }
    }

    @PostMapping("/function/save_note")
    public ResponseEntity<?> saveNote(@RequestBody Map<String, String> params) {
        try {
            String roomId = params.get("roomId");
            String note = params.get("text");
            if (roomId == null || note == null) {
                return ResponseEntity.badRequest()
                   .body(Map.of("status", "error", "message", "房间ID和笔记不能为空"));
            }
            String msg = interviewService.saveNote(roomId, note);
            if (msg.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "笔记保存成功"
                ));
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(Map.of("status", "error", "message", msg));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("status", "error", "message", "服务器内部错误"));
        }
    }
    @GetMapping("/function/get_note/{roomId}")
    public ResponseEntity<?> getNote(@PathVariable String roomId) {
        try {
            String note = interviewService.getNote(roomId);
            if (note.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body(Map.of("status", "error", "message", "笔记不存在"));
            }
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", Map.of("note", note)
            ));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Map.of("status", "error", "message", "服务器内部错误"));
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
