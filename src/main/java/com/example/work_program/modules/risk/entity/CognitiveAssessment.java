package com.example.work_program.modules.risk.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CognitiveAssessment {
    private Long id;
    private Long elderId;
    private String assessmentType;
    private Integer totalScore;
    private String riskLevel;
    private String assessmentResult;
    private String recommendations;
    private String assessor;
    private String assessmentPlace;
    private LocalDateTime assessmentTime;
    private LocalDateTime nextAssessmentDate;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
