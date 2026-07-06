package com.example.work_program.modules.elder.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.annotation.LogOperation;
import com.example.work_program.common.PageResult;
import com.example.work_program.common.Result;
import com.example.work_program.modules.elder.entity.ElderHealthRecord;
import com.example.work_program.modules.elder.service.ElderHealthRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/elder")
@LoginRequired
public class ElderHealthRecordController {

    @Autowired
    private ElderHealthRecordService elderHealthRecordService;

    @GetMapping("/list")
    public Result<PageResult<ElderHealthRecord>> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(elderHealthRecordService.findAll(name, riskLevel, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<ElderHealthRecord> getById(@PathVariable Long id) {
        return Result.success(elderHealthRecordService.findById(id));
    }

    @PostMapping("/add")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    @LogOperation("添加老人档案")
    public Result<Void> add(@Valid @RequestBody ElderHealthRecord record) {
        elderHealthRecordService.add(record);
        return Result.success("添加成功", null);
    }

    @PutMapping("/update")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    @LogOperation("更新老人档案")
    public Result<Void> update(@Valid @RequestBody ElderHealthRecord record) {
        elderHealthRecordService.update(record);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin", "doctor"})
    @LogOperation("删除老人档案")
    public Result<Void> delete(@PathVariable Long id) {
        elderHealthRecordService.deleteById(id);
        return Result.success("删除成功", null);
    }
}
