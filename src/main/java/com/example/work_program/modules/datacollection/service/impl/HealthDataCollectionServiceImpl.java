package com.example.work_program.modules.datacollection.service.impl;

import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import com.example.work_program.modules.datacollection.mapper.HealthDataCollectionMapper;
import com.example.work_program.modules.datacollection.service.HealthDataCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthDataCollectionServiceImpl implements HealthDataCollectionService {

    @Autowired
    private HealthDataCollectionMapper healthDataCollectionMapper;

    @Override
    public List<HealthDataCollection> findAll(Long elderId, String dataSource) {
        return healthDataCollectionMapper.findAll(elderId, dataSource);
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
