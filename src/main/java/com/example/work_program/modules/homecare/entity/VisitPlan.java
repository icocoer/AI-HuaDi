package com.example.work_program.modules.homecare.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VisitPlan {
    private Long id;
    private Long elderId;
    private Long doctorId;
    private String visitType;
    private LocalDate plannedDate;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}