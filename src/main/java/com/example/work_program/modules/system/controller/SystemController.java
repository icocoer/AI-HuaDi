package com.example.work_program.modules.system.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.common.Result;
import com.example.work_program.modules.system.entity.SysDict;
import com.example.work_program.modules.system.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@LoginRequired
public class SystemController {

    @Autowired
    private SysDictService sysDictService;

    @GetMapping("/dict/types")
    public Result<List<String>> getDictTypes() {
        return Result.success(sysDictService.findAllTypes());
    }

    @GetMapping("/dict/{type}")
    public Result<List<SysDict>> getDictByType(@PathVariable String type) {
        return Result.success(sysDictService.findByType(type));
    }

    @GetMapping("/dict/item/{id}")
    public Result<SysDict> getDictById(@PathVariable Long id) {
        return Result.success(sysDictService.findById(id));
    }

    @PostMapping("/dict/add")
    @LoginRequired(roles = {"admin"})
    public Result<Void> addDict(@RequestBody SysDict dict) {
        sysDictService.add(dict);
        return Result.success("添加成功", null);
    }

    @PutMapping("/dict/update")
    @LoginRequired(roles = {"admin"})
    public Result<Void> updateDict(@RequestBody SysDict dict) {
        sysDictService.update(dict);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/dict/delete/{id}")
    @LoginRequired(roles = {"admin"})
    public Result<Void> deleteDict(@PathVariable Long id) {
        sysDictService.deleteById(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getSystemStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("dictTypes", sysDictService.findAllTypes().size());
        return Result.success(stats);
    }
}
