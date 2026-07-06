package com.example.work_program.modules.intervention.service;

import com.example.work_program.modules.intervention.entity.InterventionPlan;

import java.util.List;

public interface InterventionPlanService {
    List<InterventionPlan> findAll(Long elderId, String status);
    InterventionPlan findById(Long id);
    void add(InterventionPlan plan);
    void update(InterventionPlan plan);
    void deleteById(Long id);
}
