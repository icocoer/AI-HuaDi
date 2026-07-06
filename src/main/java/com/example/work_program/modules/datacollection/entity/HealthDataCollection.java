package com.example.work_program.modules.datacollection.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HealthDataCollection {
    private Long id;

    @NotNull(message = "老人ID不能为空")
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
