package com.example.work_program.modules.analysis.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.common.Result;
import com.example.work_program.modules.analysis.dto.AnalysisStatisticsDTO;
import com.example.work_program.modules.analysis.dto.RiskDistributionDTO;
import com.example.work_program.modules.analysis.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@LoginRequired
public class AnalysisController {

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/dashboard")
    public Result<AnalysisStatisticsDTO> getDashboard() {
        return Result.success(analysisService.getDashboardStatistics());
    }

    @GetMapping("/risk-distribution")
    public Result<RiskDistributionDTO> getRiskDistribution() {
        return Result.success(analysisService.getRiskDistribution());
    }
}
