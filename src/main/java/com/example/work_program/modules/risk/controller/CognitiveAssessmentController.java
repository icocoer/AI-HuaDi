package com.example.work_program.modules.risk.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.annotation.LogOperation;
import com.example.work_program.common.PageResult;
import com.example.work_program.common.Result;
import com.example.work_program.modules.risk.entity.CognitiveAssessment;
import com.example.work_program.modules.risk.service.CognitiveAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/assessment")
@LoginRequired
public class CognitiveAssessmentController {

    @Autowired
    private CognitiveAssessmentService cognitiveAssessmentService;

    @GetMapping("/list")
    public Result<PageResult<CognitiveAssessment>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(cognitiveAssessmentService.findAll(elderId, riskLevel, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<CognitiveAssessment> getById(@PathVariable Long id) {
        return Result.success(cognitiveAssessmentService.findById(id));
    }

    @PostMapping("/add")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    @LogOperation("添加评估记录")
    public Result<Void> add(@Valid @RequestBody CognitiveAssessment assessment) {
        cognitiveAssessmentService.add(assessment);
        return Result.success("评估记录添加成功", null);
    }

    @PutMapping("/update")
    @LoginRequired(roles = {"admin", "doctor"})
    @LogOperation("更新评估记录")
    public Result<Void> update(@Valid @RequestBody CognitiveAssessment assessment) {
        cognitiveAssessmentService.update(assessment);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin", "doctor"})
    @LogOperation("删除评估记录")
    public Result<Void> delete(@PathVariable Long id) {
        cognitiveAssessmentService.deleteById(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/assess/{elderId}")
    public Result<String> assessRiskLevel(@PathVariable Long elderId) {
        String riskLevel = cognitiveAssessmentService.assessRiskLevel(elderId);
        return Result.success(riskLevel);
    }
}
