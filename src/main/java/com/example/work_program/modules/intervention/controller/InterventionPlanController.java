package com.example.work_program.modules.intervention.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.common.Result;
import com.example.work_program.modules.intervention.entity.InterventionPlan;
import com.example.work_program.modules.intervention.service.InterventionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/intervention/plan")
@LoginRequired
public class InterventionPlanController {

    @Autowired
    private InterventionPlanService interventionPlanService;

    @GetMapping("/list")
    public Result<List<InterventionPlan>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String status) {
        return Result.success(interventionPlanService.findAll(elderId, status));
    }

    @GetMapping("/{id}")
    public Result<InterventionPlan> getById(@PathVariable Long id) {
        return Result.success(interventionPlanService.findById(id));
    }

    @PostMapping("/add")
    @LoginRequired(roles = {"admin", "doctor"})
    public Result<Void> add(@RequestBody InterventionPlan plan) {
        interventionPlanService.add(plan);
        return Result.success("干预计划添加成功", null);
    }

    @PutMapping("/update")
    @LoginRequired(roles = {"admin", "doctor"})
    public Result<Void> update(@RequestBody InterventionPlan plan) {
        interventionPlanService.update(plan);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin", "doctor"})
    public Result<Void> delete(@PathVariable Long id) {
        interventionPlanService.deleteById(id);
        return Result.success("删除成功", null);
    }
}
