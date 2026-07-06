package com.example.work_program.modules.intervention.service.impl;

import com.example.work_program.modules.intervention.entity.InterventionExecution;
import com.example.work_program.modules.intervention.mapper.InterventionExecutionMapper;
import com.example.work_program.modules.intervention.service.InterventionExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterventionExecutionServiceImpl implements InterventionExecutionService {

    @Autowired
    private InterventionExecutionMapper interventionExecutionMapper;

    @Override
    public List<InterventionExecution> findAll(Long planId, Long elderId) {
        return interventionExecutionMapper.findAll(planId, elderId);
    }

    @Override
    public InterventionExecution findById(Long id) {
        return interventionExecutionMapper.findById(id);
    }

    @Override
    public void add(InterventionExecution execution) {
        interventionExecutionMapper.insert(execution);
    }

    @Override
    public void update(InterventionExecution execution) {
        interventionExecutionMapper.update(execution);
    }

    @Override
    public void deleteById(Long id) {
        interventionExecutionMapper.deleteById(id);
    }
}
