package com.example.work_program.modules.datacollection.service;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.datacollection.entity.SmartAssessment;

import java.util.List;

public interface SmartAssessmentService {
    SmartAssessment findById(Long id);
    List<SmartAssessment> findByElderId(Long elderId);
    List<SmartAssessment> findByCollectionId(Long collectionId);
    PageResult<SmartAssessment> findAll(Long elderId, String assessmentType, int pageNum, int pageSize);
    void add(SmartAssessment assessment);
    void update(SmartAssessment assessment);
    void deleteById(Long id);
    SmartAssessment findLatestByElderIdAndType(Long elderId, String type);
}
