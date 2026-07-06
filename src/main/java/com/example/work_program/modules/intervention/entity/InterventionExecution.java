package com.example.work_program.modules.intervention.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InterventionExecution {
    private Long id;

    @NotNull(message = "计划ID不能为空")
    private Long planId;

    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    @NotBlank(message = "执行类型不能为空")
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
