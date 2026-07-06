package com.example.work_program.modules.datacollection.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ImageReport {
    private Long id;
    private Long collectionId;
    private Long elderId;
    private String imageType;
    private String imageUrl;
    private String thumbUrl;
    private String reportNo;
    private String institution;
    private String doctorName;
    private LocalDate diagnosisDate;
    private String diagnosisResult;
    private String diagnosisDescription;
    private String abnormalIndicators;
    private LocalDateTime uploadTime;
}
