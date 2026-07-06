package com.example.work_program.modules.datacollection.service.impl;

import com.example.work_program.modules.datacollection.entity.HealthQuestionnaire;
import com.example.work_program.modules.datacollection.mapper.HealthQuestionnaireMapper;
import com.example.work_program.modules.datacollection.service.HealthQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthQuestionnaireServiceImpl implements HealthQuestionnaireService {

    @Autowired
    private HealthQuestionnaireMapper healthQuestionnaireMapper;

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
    public List<HealthQuestionnaire> findAll(Long elderId, String questionnaireType) {
        return healthQuestionnaireMapper.findAll(elderId, questionnaireType);
    }

    @Override
    public void add(HealthQuestionnaire questionnaire) {
        healthQuestionnaireMapper.insert(questionnaire);
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
