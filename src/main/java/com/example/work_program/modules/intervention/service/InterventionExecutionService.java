package com.example.work_program.modules.intervention.service;

import com.example.work_program.modules.intervention.entity.InterventionExecution;

import java.util.List;

public interface InterventionExecutionService {
    List<InterventionExecution> findAll(Long planId, Long elderId);
    InterventionExecution findById(Long id);
    void add(InterventionExecution execution);
    void update(InterventionExecution execution);
    void deleteById(Long id);
}
