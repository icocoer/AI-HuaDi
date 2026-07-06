package com.example.work_program.modules.analysis.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AnalysisStatisticsDTO {
    private Long totalUsers;
    private Long totalElders;
    private Long totalAssessments;
    private Long totalPlans;
    private Long totalExecutions;
    private Long totalDataCollections;
    private Map<String, Long> riskDistribution;
    private Map<String, Long> planStatusDistribution;
    private Double executionCompletionRate;
    private Long unreadWarnings;
}
