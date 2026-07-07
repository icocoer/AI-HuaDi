package com.example.work_program.modules.homecare.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class VisitRecord {
    private Long id;
    private Long planId;
    private Long elderId;
    private LocalDateTime visitDate;
    private String visitContent;
    private String healthStatus;
    private String recommendations;
    private LocalDate nextPlanDate;
    private LocalDateTime createTime;
}