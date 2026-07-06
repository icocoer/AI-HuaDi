package com.example.work_program.modules.risk.service;

import com.example.work_program.modules.risk.entity.CognitiveAssessment;

import java.util.List;

public interface CognitiveAssessmentService {
    List<CognitiveAssessment> findAll(Long elderId, String riskLevel);
    CognitiveAssessment findById(Long id);
    CognitiveAssessment findLatestByElderId(Long elderId);
    void add(CognitiveAssessment assessment);
    void update(CognitiveAssessment assessment);
    void deleteById(Long id);
    String assessRiskLevel(Long elderId);
}
