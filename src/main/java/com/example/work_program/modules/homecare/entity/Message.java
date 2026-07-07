package com.example.work_program.modules.homecare.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String messageType;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}