package com.example.work_program.modules.intervention.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.annotation.LogOperation;
import com.example.work_program.common.PageResult;
import com.example.work_program.common.Result;
import com.example.work_program.modules.intervention.entity.InterventionExecution;
import com.example.work_program.modules.intervention.service.InterventionExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/intervention/execution")
@LoginRequired
public class InterventionExecutionController {

    @Autowired
    private InterventionExecutionService interventionExecutionService;

    @GetMapping("/list")
    public Result<PageResult<InterventionExecution>> list(
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long elderId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(interventionExecutionService.findAll(planId, elderId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<InterventionExecution> getById(@PathVariable Long id) {
        return Result.success(interventionExecutionService.findById(id));
    }

    @PostMapping("/add")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    @LogOperation("添加执行记录")
    public Result<Void> add(@Valid @RequestBody InterventionExecution execution) {
        interventionExecutionService.add(execution);
        return Result.success("执行记录添加成功", null);
    }

    @PutMapping("/update")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    @LogOperation("更新执行记录")
    public Result<Void> update(@Valid @RequestBody InterventionExecution execution) {
        interventionExecutionService.update(execution);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin", "doctor"})
    @LogOperation("删除执行记录")
    public Result<Void> delete(@PathVariable Long id) {
        interventionExecutionService.deleteById(id);
        return Result.success("删除成功", null);
    }
}
