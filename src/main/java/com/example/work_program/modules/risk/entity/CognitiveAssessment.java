package com.example.work_program.modules.risk.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CognitiveAssessment {
    private Long id;

    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    @NotBlank(message = "评估类型不能为空")
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
