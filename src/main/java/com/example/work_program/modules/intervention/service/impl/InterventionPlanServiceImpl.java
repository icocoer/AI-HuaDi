package com.example.work_program.modules.intervention.service.impl;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.intervention.entity.InterventionPlan;
import com.example.work_program.modules.intervention.mapper.InterventionPlanMapper;
import com.example.work_program.modules.intervention.service.InterventionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class InterventionPlanServiceImpl implements InterventionPlanService {

    @Autowired
    private InterventionPlanMapper interventionPlanMapper;

    @Override
    public PageResult<InterventionPlan> findAll(Long elderId, String status, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = interventionPlanMapper.count(elderId, status);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        List<InterventionPlan> list = interventionPlanMapper.findAll(elderId, status, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public InterventionPlan findById(Long id) {
        return interventionPlanMapper.findById(id);
    }

    @Override
    public void add(InterventionPlan plan) {
        interventionPlanMapper.insert(plan);
    }

    @Override
    public void update(InterventionPlan plan) {
        interventionPlanMapper.update(plan);
    }

    @Override
    public void deleteById(Long id) {
        interventionPlanMapper.deleteById(id);
    }
}
