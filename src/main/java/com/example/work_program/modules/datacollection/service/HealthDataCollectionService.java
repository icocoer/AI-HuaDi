package com.example.work_program.modules.datacollection.service;

import com.example.work_program.modules.datacollection.entity.HealthDataCollection;

import java.util.List;

public interface HealthDataCollectionService {
    List<HealthDataCollection> findAll(Long elderId, String dataSource);
    HealthDataCollection findById(Long id);
    void add(HealthDataCollection data);
    void update(HealthDataCollection data);
    void deleteById(Long id);
}
