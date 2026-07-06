package com.example.work_program.modules.intervention.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.common.Result;
import com.example.work_program.modules.intervention.entity.InterventionExecution;
import com.example.work_program.modules.intervention.service.InterventionExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intervention/execution")
@LoginRequired
public class InterventionExecutionController {

    @Autowired
    private InterventionExecutionService interventionExecutionService;

    @GetMapping("/list")
    public Result<List<InterventionExecution>> list(
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) Long elderId) {
        return Result.success(interventionExecutionService.findAll(planId, elderId));
    }

    @GetMapping("/{id}")
    public Result<InterventionExecution> getById(@PathVariable Long id) {
        return Result.success(interventionExecutionService.findById(id));
    }

    @PostMapping("/add")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    public Result<Void> add(@RequestBody InterventionExecution execution) {
        interventionExecutionService.add(execution);
        return Result.success("执行记录添加成功", null);
    }

    @PutMapping("/update")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    public Result<Void> update(@RequestBody InterventionExecution execution) {
        interventionExecutionService.update(execution);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin", "doctor"})
    public Result<Void> delete(@PathVariable Long id) {
        interventionExecutionService.deleteById(id);
        return Result.success("删除成功", null);
    }
}
