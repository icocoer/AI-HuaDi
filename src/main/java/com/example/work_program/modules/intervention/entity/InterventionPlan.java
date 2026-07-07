package com.example.work_program.modules.intervention.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InterventionPlan {
    private Long id;

    @NotNull(message = "老人ID不能为空")
    private Long elderId;

    @NotBlank(message = "计划名称不能为空")
    private String planName;

    private String planType;
    private String riskLevel;
    private String cognitiveTraining;
    private String lifestyleIntervention;
    private String rehabilitationPlan;
    private String goals;
    private LocalDate startDate;
    private LocalDate endDate;
    private String responsibleDoctor;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
