package com.example.work_program.modules.system.service.impl;

import com.example.work_program.modules.system.entity.SysDict;
import com.example.work_program.modules.system.mapper.SysDictMapper;
import com.example.work_program.modules.system.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysDictServiceImpl implements SysDictService {

    @Autowired
    private SysDictMapper sysDictMapper;

    @Override
    public List<SysDict> findByType(String dictType) {
        return sysDictMapper.findByType(dictType);
    }

    @Override
    public SysDict findById(Long id) {
        return sysDictMapper.findById(id);
    }

    @Override
    public List<String> findAllTypes() {
        return sysDictMapper.findAllTypes();
    }

    @Override
    public void add(SysDict dict) {
        sysDictMapper.insert(dict);
    }

    @Override
    public void update(SysDict dict) {
        sysDictMapper.update(dict);
    }

    @Override
    public void deleteById(Long id) {
        sysDictMapper.deleteById(id);
    }
}
