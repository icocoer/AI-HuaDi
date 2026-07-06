package com.example.work_program.modules.system.service;

import com.example.work_program.modules.system.entity.SysDict;
import java.util.List;

public interface SysDictService {
    List<SysDict> findByType(String dictType);
    SysDict findById(Long id);
    List<String> findAllTypes();
    void add(SysDict dict);
    void update(SysDict dict);
    void deleteById(Long id);
}
