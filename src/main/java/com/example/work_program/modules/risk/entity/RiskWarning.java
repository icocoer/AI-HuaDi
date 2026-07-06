package com.example.work_program.modules.risk.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RiskWarning {
    private Long id;
    private Long elderId;
    private String elderName;
    private String riskLevel;
    private String warningType;
    private String warningMsg;
    private Integer isRead;
    private LocalDateTime createTime;
}
