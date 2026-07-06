package com.example.work_program.modules.analysis.service.impl;

import com.example.work_program.modules.analysis.dto.AnalysisStatisticsDTO;
import com.example.work_program.modules.analysis.dto.RiskDistributionDTO;
import com.example.work_program.modules.system.mapper.UserMapper;
import com.example.work_program.modules.elder.mapper.ElderHealthRecordMapper;
import com.example.work_program.modules.risk.mapper.CognitiveAssessmentMapper;
import com.example.work_program.modules.risk.mapper.RiskWarningMapper;
import com.example.work_program.modules.intervention.mapper.InterventionPlanMapper;
import com.example.work_program.modules.intervention.mapper.InterventionExecutionMapper;
import com.example.work_program.modules.datacollection.mapper.HealthDataCollectionMapper;
import com.example.work_program.modules.analysis.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ElderHealthRecordMapper elderHealthRecordMapper;
    @Autowired
    private CognitiveAssessmentMapper cognitiveAssessmentMapper;
    @Autowired
    private InterventionPlanMapper interventionPlanMapper;
    @Autowired
    private InterventionExecutionMapper interventionExecutionMapper;
    @Autowired
    private HealthDataCollectionMapper healthDataCollectionMapper;
    @Autowired
    private RiskWarningMapper riskWarningMapper;

    @Override
    public AnalysisStatisticsDTO getDashboardStatistics() {
        AnalysisStatisticsDTO dto = new AnalysisStatisticsDTO();

        dto.setTotalUsers((long) userMapper.findAll(null, null).size());
        dto.setTotalElders((long) elderHealthRecordMapper.findAll(null, null).size());
        dto.setTotalAssessments((long) cognitiveAssessmentMapper.findAll(null, null).size());
        dto.setTotalPlans((long) interventionPlanMapper.findAll(null, null).size());
        dto.setTotalExecutions((long) interventionExecutionMapper.findAll(null, null).size());
        dto.setTotalDataCollections((long) healthDataCollectionMapper.findAll(null, null).size());

        Map<String, Long> riskDist = new HashMap<>();
        riskDist.put("low", elderHealthRecordMapper.findAll(null, "low").stream().count());
        riskDist.put("medium", elderHealthRecordMapper.findAll(null, "medium").stream().count());
        riskDist.put("high", elderHealthRecordMapper.findAll(null, "high").stream().count());
        dto.setRiskDistribution(riskDist);

        Map<String, Long> planStatus = new HashMap<>();
        planStatus.put("pending", interventionPlanMapper.findAll(null, "pending").stream().count());
        planStatus.put("in_progress", interventionPlanMapper.findAll(null, "in_progress").stream().count());
        planStatus.put("completed", interventionPlanMapper.findAll(null, "completed").stream().count());
        dto.setPlanStatusDistribution(planStatus);

        long totalPlans = dto.getTotalPlans();
        long inProgress = planStatus.getOrDefault("in_progress", 0L);
        dto.setExecutionCompletionRate(totalPlans > 0 ? (double) inProgress / totalPlans : 0.0);

        dto.setUnreadWarnings(riskWarningMapper.countUnread());

        return dto;
    }

    @Override
    public RiskDistributionDTO getRiskDistribution() {
        RiskDistributionDTO dto = new RiskDistributionDTO();
        Map<String, Long> counts = new HashMap<>();
        counts.put("low", elderHealthRecordMapper.findAll(null, "low").stream().count());
        counts.put("medium", elderHealthRecordMapper.findAll(null, "medium").stream().count());
        counts.put("high", elderHealthRecordMapper.findAll(null, "high").stream().count());
        dto.setRiskLevelCounts(counts);
        return dto;
    }
}
