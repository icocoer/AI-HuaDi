package com.example.work_program.modules.intervention.service.impl;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.intervention.entity.InterventionExecution;
import com.example.work_program.modules.intervention.mapper.InterventionExecutionMapper;
import com.example.work_program.modules.intervention.service.InterventionExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class InterventionExecutionServiceImpl implements InterventionExecutionService {

    @Autowired
    private InterventionExecutionMapper interventionExecutionMapper;

    @Override
    public PageResult<InterventionExecution> findAll(Long planId, Long elderId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = interventionExecutionMapper.count(planId, elderId);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        List<InterventionExecution> list = interventionExecutionMapper.findAll(planId, elderId, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
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
