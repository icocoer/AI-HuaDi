package com.example.work_program.modules.analysis.service;

import com.example.work_program.modules.analysis.dto.AnalysisStatisticsDTO;
import com.example.work_program.modules.analysis.dto.RiskDistributionDTO;

public interface AnalysisService {
    AnalysisStatisticsDTO getDashboardStatistics();
    RiskDistributionDTO getRiskDistribution();
}
