package com.example.work_program.modules.datacollection.service;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.datacollection.entity.HealthDataCollection;

public interface HealthDataCollectionService {
    PageResult<HealthDataCollection> findAll(Long elderId, String dataSource, int pageNum, int pageSize);
    HealthDataCollection findById(Long id);
    void add(HealthDataCollection data);
    void update(HealthDataCollection data);
    void deleteById(Long id);
}
