package com.example.work_program.modules.datacollection.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthQuestionnaire {
    private Long id;
    private Long collectionId;
    private Long elderId;
    private String questionnaireType;
    private String questions;
    private String answers;
    private String riskFactors;
    private String summary;
    private String surveyor;
    private LocalDateTime surveyTime;
    private LocalDateTime createTime;
}
