package com.example.work_program.modules.intervention.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class InterventionPlan {
    private Long id;
    private Long elderId;
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
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
