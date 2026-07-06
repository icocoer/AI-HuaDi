package com.example.work_program.modules.risk.service;

import com.example.work_program.modules.risk.entity.RiskWarning;
import java.util.List;

public interface RiskWarningService {
    List<RiskWarning> findByElderId(Long elderId);
    List<RiskWarning> findUnread();
    List<RiskWarning> findAll();
    RiskWarning findById(Long id);
    void add(RiskWarning warning);
    void markAsRead(Long id);
    Long countUnread();
    void checkAndCreateWarning(Long elderId, String riskLevel, String assessmentResult);
}
