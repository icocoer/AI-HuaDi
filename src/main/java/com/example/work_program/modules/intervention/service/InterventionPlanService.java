package com.example.work_program.modules.intervention.service;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.intervention.entity.InterventionPlan;

public interface InterventionPlanService {
    PageResult<InterventionPlan> findAll(Long elderId, String status, int pageNum, int pageSize);
    InterventionPlan findById(Long id);
    void add(InterventionPlan plan);
    void update(InterventionPlan plan);
    void deleteById(Long id);
}
