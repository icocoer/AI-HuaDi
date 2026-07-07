package com.example.work_program.modules.homecare.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthAlert {
    private Long id;
    private Long elderId;
    private String alertType;
    private String alertLevel;
    private String alertMessage;
    private Integer isRead;
    private LocalDateTime createTime;
}