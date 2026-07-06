package com.example.work_program.modules.datacollection.service.impl;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import com.example.work_program.modules.datacollection.mapper.HealthDataCollectionMapper;
import com.example.work_program.modules.datacollection.service.HealthDataCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class HealthDataCollectionServiceImpl implements HealthDataCollectionService {

    @Autowired
    private HealthDataCollectionMapper healthDataCollectionMapper;

    @Override
    public PageResult<HealthDataCollection> findAll(Long elderId, String dataSource, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = healthDataCollectionMapper.count(elderId, dataSource);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        List<HealthDataCollection> list = healthDataCollectionMapper.findAll(elderId, dataSource, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public HealthDataCollection findById(Long id) {
        return healthDataCollectionMapper.findById(id);
    }

    @Override
    public void add(HealthDataCollection data) {
        healthDataCollectionMapper.insert(data);
    }

    @Override
    public void update(HealthDataCollection data) {
        healthDataCollectionMapper.update(data);
    }

    @Override
    public void deleteById(Long id) {
        healthDataCollectionMapper.deleteById(id);
    }
}
