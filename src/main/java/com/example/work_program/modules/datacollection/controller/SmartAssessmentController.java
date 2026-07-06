package com.example.work_program.modules.datacollection.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.annotation.LogOperation;
import com.example.work_program.common.Result;
import com.example.work_program.modules.datacollection.entity.SmartAssessment;
import com.example.work_program.modules.datacollection.service.SmartAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能评估管理控制器
 */
@RestController
@RequestMapping("/api/data/smart")
@LoginRequired
public class SmartAssessmentController {

    @Autowired
    private SmartAssessmentService smartAssessmentService;

    /**
     * 分页查询智能评估记录
     */
    @GetMapping("/list")
    public Result<List<SmartAssessment>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String assessmentType) {
        return Result.success(smartAssessmentService.findAll(elderId, assessmentType));
    }

    /**
     * 获取智能评估详情
     */
    @GetMapping("/{id}")
    public Result<SmartAssessment> getById(@PathVariable Long id) {
        return Result.success(smartAssessmentService.findById(id));
    }

    /**
     * 根据老人ID查询智能评估列表
     */
    @GetMapping("/elder/{elderId}")
    public Result<List<SmartAssessment>> getByElderId(@PathVariable Long elderId) {
        return Result.success(smartAssessmentService.findByElderId(elderId));
    }

    /**
     * 添加智能评估记录
     */
    @PostMapping("/add")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    @LogOperation("添加智能评估")
    public Result<Void> add(@Valid @RequestBody SmartAssessment assessment) {
        smartAssessmentService.add(assessment);
        return Result.success("智能评估记录添加成功", null);
    }

    /**
     * 更新智能评估记录
     */
    @PutMapping("/update")
    @LoginRequired(roles = {"admin", "doctor"})
    @LogOperation("更新智能评估")
    public Result<Void> update(@Valid @RequestBody SmartAssessment assessment) {
        smartAssessmentService.update(assessment);
        return Result.success("更新成功", null);
    }

    /**
     * 删除智能评估记录
     */
    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin", "doctor"})
    @LogOperation("删除智能评估")
    public Result<Void> delete(@PathVariable Long id) {
        smartAssessmentService.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取老人最新智能评估
     */
    @GetMapping("/latest/{elderId}")
    public Result<SmartAssessment> getLatest(@PathVariable Long elderId,
                                             @RequestParam(required = false, defaultValue = "cognitive") String type) {
        return Result.success(smartAssessmentService.findLatestByElderIdAndType(elderId, type));
    }
}
