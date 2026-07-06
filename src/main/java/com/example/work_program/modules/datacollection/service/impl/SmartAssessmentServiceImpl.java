package com.example.work_program.modules.datacollection.service.impl;

import com.example.work_program.modules.datacollection.entity.SmartAssessment;
import com.example.work_program.modules.datacollection.mapper.SmartAssessmentMapper;
import com.example.work_program.modules.datacollection.service.SmartAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmartAssessmentServiceImpl implements SmartAssessmentService {

    @Autowired
    private SmartAssessmentMapper smartAssessmentMapper;

    @Override
    public SmartAssessment findById(Long id) {
        return smartAssessmentMapper.findById(id);
    }

    @Override
    public List<SmartAssessment> findByElderId(Long elderId) {
        return smartAssessmentMapper.findByElderId(elderId);
    }

    @Override
    public List<SmartAssessment> findByCollectionId(Long collectionId) {
        return smartAssessmentMapper.findByCollectionId(collectionId);
    }

    @Override
    public List<SmartAssessment> findAll(Long elderId, String assessmentType) {
        return smartAssessmentMapper.findAll(elderId, assessmentType);
    }

    @Override
    public void add(SmartAssessment assessment) {
        smartAssessmentMapper.insert(assessment);
    }

    @Override
    public void update(SmartAssessment assessment) {
        smartAssessmentMapper.update(assessment);
    }

    @Override
    public void deleteById(Long id) {
        smartAssessmentMapper.deleteById(id);
    }

    @Override
    public SmartAssessment findLatestByElderIdAndType(Long elderId, String type) {
        return smartAssessmentMapper.findLatestByElderIdAndType(elderId, type);
    }
}
