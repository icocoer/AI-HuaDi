package com.example.work_program.modules.datacollection.service.impl;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import com.example.work_program.modules.datacollection.entity.HealthQuestionnaire;
import com.example.work_program.modules.datacollection.mapper.HealthDataCollectionMapper;
import com.example.work_program.modules.datacollection.mapper.HealthQuestionnaireMapper;
import com.example.work_program.modules.datacollection.service.HealthQuestionnaireService;
import com.example.work_program.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class HealthQuestionnaireServiceImpl implements HealthQuestionnaireService {

    @Autowired
    private HealthQuestionnaireMapper healthQuestionnaireMapper;

    @Autowired
    private HealthDataCollectionMapper healthDataCollectionMapper;

    @Autowired
    private SnowflakeIdGenerator idGenerator;

    @Override
    public HealthQuestionnaire findById(Long id) {
        return healthQuestionnaireMapper.findById(id);
    }

    @Override
    public List<HealthQuestionnaire> findByElderId(Long elderId) {
        return healthQuestionnaireMapper.findByElderId(elderId);
    }

    @Override
    public List<HealthQuestionnaire> findByCollectionId(Long collectionId) {
        return healthQuestionnaireMapper.findByCollectionId(collectionId);
    }

    @Override
    public PageResult<HealthQuestionnaire> findAll(Long elderId, String questionnaireType, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = healthQuestionnaireMapper.count(elderId, questionnaireType);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        List<HealthQuestionnaire> list = healthQuestionnaireMapper.findAll(elderId, questionnaireType, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public void add(HealthQuestionnaire questionnaire) {
        questionnaire.setId(idGenerator.nextId());
        healthQuestionnaireMapper.insert(questionnaire);

        // 同时写入 health_data_collection 表，供健康监测页面使用
        HealthDataCollection collection = new HealthDataCollection();
        collection.setId(idGenerator.nextId());
        collection.setElderId(questionnaire.getElderId());
        collection.setDataSource("questionnaire");
        collection.setDataType(questionnaire.getQuestionnaireType());
        collection.setDataContent(questionnaire.getSummary());
        collection.setCollector(questionnaire.getSurveyor());
        collection.setCollectionDate(questionnaire.getSurveyTime() != null ? questionnaire.getSurveyTime().toLocalDate() : null);
        healthDataCollectionMapper.insert(collection);
    }

    @Override
    public void update(HealthQuestionnaire questionnaire) {
        healthQuestionnaireMapper.update(questionnaire);
    }

    @Override
    public void deleteById(Long id) {
        healthQuestionnaireMapper.deleteById(id);
    }
}
