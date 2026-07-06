package com.example.work_program.modules.risk.service.impl;

import com.example.work_program.modules.risk.entity.RiskWarning;
import com.example.work_program.modules.risk.mapper.RiskWarningMapper;
import com.example.work_program.modules.risk.service.RiskWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskWarningServiceImpl implements RiskWarningService {

    @Autowired
    private RiskWarningMapper riskWarningMapper;

    @Override
    public List<RiskWarning> findByElderId(Long elderId) {
        return riskWarningMapper.findByElderId(elderId);
    }

    @Override
    public List<RiskWarning> findUnread() {
        return riskWarningMapper.findUnread();
    }

    @Override
    public List<RiskWarning> findAll() {
        return riskWarningMapper.findAll();
    }

    @Override
    public RiskWarning findById(Long id) {
        return riskWarningMapper.findById(id);
    }

    @Override
    public void add(RiskWarning warning) {
        riskWarningMapper.insert(warning);
    }

    @Override
    public void markAsRead(Long id) {
        riskWarningMapper.markAsRead(id);
    }

    @Override
    public Long countUnread() {
        return riskWarningMapper.countUnread();
    }

    @Override
    public void checkAndCreateWarning(Long elderId, String riskLevel, String assessmentResult) {
        if ("high".equals(riskLevel) || "severe".equals(riskLevel)) {
            RiskWarning warning = new RiskWarning();
            warning.setElderId(elderId);
            warning.setRiskLevel(riskLevel);
            warning.setWarningType("risk_alert");
            warning.setWarningMsg("老人风险等级为" + riskLevel + "，评估结果: " + assessmentResult + "，请及时关注并制定干预计划。");
            riskWarningMapper.insert(warning);
        }
    }
}
