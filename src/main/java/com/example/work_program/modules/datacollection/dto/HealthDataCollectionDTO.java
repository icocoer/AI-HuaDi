package com.example.work_program.modules.datacollection.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class HealthDataCollectionDTO {
    private Long id;
    private Long elderId;
    private String elderName;
    private String dataSource;
    private String dataSourceDesc;
    private String dataType;
    private String dataTypeDesc;
    private String dataContent;
    private String attachmentUrl;
    private LocalDate collectionDate;
    private String collector;
    private String remark;
}
