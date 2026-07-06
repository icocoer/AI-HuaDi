package com.example.work_program.modules.elder.service.impl;

import com.example.work_program.modules.elder.entity.ElderHealthRecord;
import com.example.work_program.modules.elder.mapper.ElderHealthRecordMapper;
import com.example.work_program.modules.elder.service.ElderHealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ElderHealthRecordServiceImpl implements ElderHealthRecordService {

    @Autowired
    private ElderHealthRecordMapper elderHealthRecordMapper;

    @Override
    public List<ElderHealthRecord> findAll(String name, String riskLevel) {
        return elderHealthRecordMapper.findAll(name, riskLevel);
    }

    @Override
    public ElderHealthRecord findById(Long id) {
        return elderHealthRecordMapper.findById(id);
    }

    @Override
    public void add(ElderHealthRecord record) {
        elderHealthRecordMapper.insert(record);
    }

    @Override
    public void update(ElderHealthRecord record) {
        elderHealthRecordMapper.update(record);
    }

    @Override
    public void deleteById(Long id) {
        elderHealthRecordMapper.deleteById(id);
    }
}
