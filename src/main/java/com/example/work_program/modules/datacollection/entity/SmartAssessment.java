package com.example.work_program.modules.datacollection.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SmartAssessment {
    private Long id;
    private Long collectionId;
    private Long elderId;
    private String assessmentType;
    private String assessmentItems;
    private Integer totalScore;
    private String scoreLevel;
    private String assessmentResult;
    private String recommendations;
    private String assessor;
    private String assessmentDevice;
    private LocalDateTime assessmentTime;
    private LocalDateTime createTime;
}
