package com.example.work_program.modules.datacollection.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HealthDataCollection {
    private Long id;
    private Long elderId;
    private String dataSource;
    private String dataType;
    private String dataContent;
    private String attachmentUrl;
    private LocalDate collectionDate;
    private String collector;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
