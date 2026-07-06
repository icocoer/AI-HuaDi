package com.example.work_program.modules.elder.service;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.elder.entity.ElderHealthRecord;

public interface ElderHealthRecordService {
    PageResult<ElderHealthRecord> findAll(String name, String riskLevel, int pageNum, int pageSize);
    ElderHealthRecord findById(Long id);
    void add(ElderHealthRecord record);
    void update(ElderHealthRecord record);
    void deleteById(Long id);
}
