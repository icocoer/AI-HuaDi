package com.example.work_program.modules.intervention.service;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.intervention.entity.InterventionExecution;

public interface InterventionExecutionService {
    PageResult<InterventionExecution> findAll(Long planId, Long elderId, int pageNum, int pageSize);
    InterventionExecution findById(Long id);
    void add(InterventionExecution execution);
    void update(InterventionExecution execution);
    void deleteById(Long id);
}
