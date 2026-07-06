package com.example.work_program.modules.elder.service.impl;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.elder.entity.ElderHealthRecord;
import com.example.work_program.modules.elder.mapper.ElderHealthRecordMapper;
import com.example.work_program.modules.elder.service.ElderHealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ElderHealthRecordServiceImpl implements ElderHealthRecordService {

    @Autowired
    private ElderHealthRecordMapper elderHealthRecordMapper;

    @Override
    public PageResult<ElderHealthRecord> findAll(String name, String riskLevel, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = elderHealthRecordMapper.count(name, riskLevel);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        List<ElderHealthRecord> list = elderHealthRecordMapper.findAll(name, riskLevel, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
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
