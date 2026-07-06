package com.example.work_program.modules.analysis.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class RiskDistributionDTO {
    private Map<String, Long> riskLevelCounts;
    private List<TrendItem> trends;

    @Data
    public static class TrendItem {
        private String date;
        private Long count;
    }
}
