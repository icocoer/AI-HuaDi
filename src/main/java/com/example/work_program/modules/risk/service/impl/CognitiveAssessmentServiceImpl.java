package com.example.work_program.modules.risk.service.impl;

import com.example.work_program.modules.risk.entity.CognitiveAssessment;
import com.example.work_program.modules.risk.mapper.CognitiveAssessmentMapper;
import com.example.work_program.modules.risk.service.CognitiveAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CognitiveAssessmentServiceImpl implements CognitiveAssessmentService {

    @Autowired
    private CognitiveAssessmentMapper cognitiveAssessmentMapper;

    @Override
    public List<CognitiveAssessment> findAll(Long elderId, String riskLevel) {
        return cognitiveAssessmentMapper.findAll(elderId, riskLevel);
    }

    @Override
    public CognitiveAssessment findById(Long id) {
        return cognitiveAssessmentMapper.findById(id);
    }

    @Override
    public CognitiveAssessment findLatestByElderId(Long elderId) {
        return cognitiveAssessmentMapper.findLatestByElderId(elderId);
    }

    @Override
    public void add(CognitiveAssessment assessment) {
        cognitiveAssessmentMapper.insert(assessment);
    }

    @Override
    public void update(CognitiveAssessment assessment) {
        cognitiveAssessmentMapper.update(assessment);
    }

    @Override
    public void deleteById(Long id) {
        cognitiveAssessmentMapper.deleteById(id);
    }

    @Override
    public String assessRiskLevel(Long elderId) {
        CognitiveAssessment assessment = cognitiveAssessmentMapper.findLatestByElderId(elderId);
        if (assessment == null) {
            return "无评估数据";
        }
        Integer score = assessment.getTotalScore();
        if (score >= 26) {
            return "正常";
        } else if (score >= 18) {
            return "轻度认知障碍";
        } else if (score >= 10) {
            return "中度认知障碍";
        } else {
            return "重度认知障碍";
        }
    }
}
