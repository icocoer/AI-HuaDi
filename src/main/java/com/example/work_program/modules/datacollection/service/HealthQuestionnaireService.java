package com.example.work_program.modules.datacollection.service;

import com.example.work_program.modules.datacollection.entity.HealthQuestionnaire;

import java.util.List;

public interface HealthQuestionnaireService {
    HealthQuestionnaire findById(Long id);
    List<HealthQuestionnaire> findByElderId(Long elderId);
    List<HealthQuestionnaire> findByCollectionId(Long collectionId);
    List<HealthQuestionnaire> findAll(Long elderId, String questionnaireType);
    void add(HealthQuestionnaire questionnaire);
    void update(HealthQuestionnaire questionnaire);
    void deleteById(Long id);
}
