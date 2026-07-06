package com.example.work_program.modules.elder.service;

import com.example.work_program.modules.elder.entity.ElderHealthRecord;

import java.util.List;

public interface ElderHealthRecordService {
    List<ElderHealthRecord> findAll(String name, String riskLevel);
    ElderHealthRecord findById(Long id);
    void add(ElderHealthRecord record);
    void update(ElderHealthRecord record);
    void deleteById(Long id);
}
