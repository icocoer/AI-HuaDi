package com.example.work_program.modules.risk.service;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.risk.entity.CognitiveAssessment;

public interface CognitiveAssessmentService {
    PageResult<CognitiveAssessment> findAll(Long elderId, String riskLevel, int pageNum, int pageSize);
    CognitiveAssessment findById(Long id);
    CognitiveAssessment findLatestByElderId(Long elderId);
    void add(CognitiveAssessment assessment);
    void update(CognitiveAssessment assessment);
    void deleteById(Long id);
    String assessRiskLevel(Long elderId);
}
