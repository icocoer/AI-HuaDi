package com.example.work_program.modules.intervention.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InterventionExecution {
    private Long id;
    private Long planId;
    private Long elderId;
    private String executionType;
    private String content;
    private LocalDate executionDate;
    private Integer duration;
    private String effectEvaluation;
    private String evaluator;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
