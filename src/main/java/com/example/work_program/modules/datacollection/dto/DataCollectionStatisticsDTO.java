package com.example.work_program.modules.datacollection.dto;

import lombok.Data;

@Data
public class DataCollectionStatisticsDTO {
    private Long totalCount;
    private Long smartCount;
    private Long questionnaireCount;
    private Long imageCount;
    private Long todayCount;
    private Long weekCount;
    private Long monthCount;
}
