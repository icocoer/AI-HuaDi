package com.example.work_program.modules.datacollection.dto;

import lombok.Data;

@Data
public class DataCollectionQueryDTO {
    private Long elderId;
    private String dataSource;
    private String dataType;
    private String startDate;
    private String endDate;
    private String collector;
}
