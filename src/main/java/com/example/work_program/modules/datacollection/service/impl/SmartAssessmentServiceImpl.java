package com.example.work_program.modules.datacollection.service.impl;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import com.example.work_program.modules.datacollection.entity.SmartAssessment;
import com.example.work_program.modules.datacollection.mapper.HealthDataCollectionMapper;
import com.example.work_program.modules.datacollection.mapper.SmartAssessmentMapper;
import com.example.work_program.modules.datacollection.service.SmartAssessmentService;
import com.example.work_program.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SmartAssessmentServiceImpl implements SmartAssessmentService {

    @Autowired
    private SmartAssessmentMapper smartAssessmentMapper;

    @Autowired
    private HealthDataCollectionMapper healthDataCollectionMapper;

    @Autowired
    private SnowflakeIdGenerator idGenerator;

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
    public PageResult<SmartAssessment> findAll(Long elderId, String assessmentType, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = smartAssessmentMapper.count(elderId, assessmentType);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        List<SmartAssessment> list = smartAssessmentMapper.findAll(elderId, assessmentType, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public void add(SmartAssessment assessment) {
        assessment.setId(idGenerator.nextId());
        smartAssessmentMapper.insert(assessment);

        // 同时写入 health_data_collection 表
        HealthDataCollection collection = new HealthDataCollection();
        collection.setId(idGenerator.nextId());
        collection.setElderId(assessment.getElderId());
        collection.setDataSource("smart");
        collection.setDataType(assessment.getAssessmentType());
        collection.setDataContent(assessment.getAssessmentResult());
        collection.setCollector(assessment.getAssessor());
        collection.setCollectionDate(assessment.getAssessmentTime() != null ? assessment.getAssessmentTime().toLocalDate() : null);
        healthDataCollectionMapper.insert(collection);
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
