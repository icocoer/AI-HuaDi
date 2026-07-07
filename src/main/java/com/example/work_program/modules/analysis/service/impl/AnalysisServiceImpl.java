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
import com.example.work_program.modules.homecare.mapper.*;
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
    @Autowired
    private VisitPlanMapper visitPlanMapper;
    @Autowired
    private HealthAlertMapper healthAlertMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public AnalysisStatisticsDTO getDashboardStatistics() {
        AnalysisStatisticsDTO dto = new AnalysisStatisticsDTO();

        dto.setTotalUsers(userMapper.count(null, null));
        dto.setTotalElders(elderHealthRecordMapper.count(null, null));
        dto.setTotalAssessments(cognitiveAssessmentMapper.count(null, null));
        dto.setTotalPlans(interventionPlanMapper.count(null, null));
        dto.setTotalExecutions(interventionExecutionMapper.count(null, null));
        dto.setTotalDataCollections(healthDataCollectionMapper.count(null, null));

        Map<String, Long> riskDist = new HashMap<>();
        riskDist.put("low", elderHealthRecordMapper.count(null, "low"));
        riskDist.put("medium", elderHealthRecordMapper.count(null, "medium"));
        riskDist.put("high", elderHealthRecordMapper.count(null, "high"));
        dto.setRiskDistribution(riskDist);

        Map<String, Long> planStatus = new HashMap<>();
        planStatus.put("pending", interventionPlanMapper.count(null, "pending"));
        planStatus.put("in_progress", interventionPlanMapper.count(null, "in_progress"));
        planStatus.put("completed", interventionPlanMapper.count(null, "completed"));
        dto.setPlanStatusDistribution(planStatus);

        long totalPlans = dto.getTotalPlans();
        long inProgress = planStatus.getOrDefault("in_progress", 0L);
        dto.setExecutionCompletionRate(totalPlans > 0 ? (double) inProgress / totalPlans : 0.0);

        dto.setUnreadWarnings(riskWarningMapper.countUnread());

        // 家庭健康助手统计
        dto.setTotalVisitPlans(visitPlanMapper.count(null, null, null));
        dto.setUnreadAlerts(healthAlertMapper.countUnread());
        dto.setUnreadMessages(messageMapper.countUnread(null));

        return dto;
    }

    @Override
    public RiskDistributionDTO getRiskDistribution() {
        RiskDistributionDTO dto = new RiskDistributionDTO();
        Map<String, Long> counts = new HashMap<>();
        counts.put("low", elderHealthRecordMapper.count(null, "low"));
        counts.put("medium", elderHealthRecordMapper.count(null, "medium"));
        counts.put("high", elderHealthRecordMapper.count(null, "high"));
        dto.setRiskLevelCounts(counts);
        return dto;
    }
}
