package com.example.work_program.modules.datacollection.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.common.Result;
import com.example.work_program.modules.datacollection.entity.HealthQuestionnaire;
import com.example.work_program.modules.datacollection.service.HealthQuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 健康问询管理控制器
 */
@RestController
@RequestMapping("/api/data/questionnaire")
@LoginRequired
public class HealthQuestionnaireController {

    @Autowired
    private HealthQuestionnaireService healthQuestionnaireService;

    /**
     * 分页查询健康问询记录
     */
    @GetMapping("/list")
    public Result<List<HealthQuestionnaire>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String questionnaireType) {
        return Result.success(healthQuestionnaireService.findAll(elderId, questionnaireType));
    }

    /**
     * 获取健康问询详情
     */
    @GetMapping("/{id}")
    public Result<HealthQuestionnaire> getById(@PathVariable Long id) {
        return Result.success(healthQuestionnaireService.findById(id));
    }

    /**
     * 根据老人ID查询健康问询列表
     */
    @GetMapping("/elder/{elderId}")
    public Result<List<HealthQuestionnaire>> getByElderId(@PathVariable Long elderId) {
        return Result.success(healthQuestionnaireService.findByElderId(elderId));
    }

    /**
     * 添加健康问询记录
     */
    @PostMapping("/add")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    public Result<Void> add(@RequestBody HealthQuestionnaire questionnaire) {
        healthQuestionnaireService.add(questionnaire);
        return Result.success("健康问询记录添加成功", null);
    }

    /**
     * 更新健康问询记录
     */
    @PutMapping("/update")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    public Result<Void> update(@RequestBody HealthQuestionnaire questionnaire) {
        healthQuestionnaireService.update(questionnaire);
        return Result.success("更新成功", null);
    }

    /**
     * 删除健康问询记录
     */
    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin", "doctor"})
    public Result<Void> delete(@PathVariable Long id) {
        healthQuestionnaireService.deleteById(id);
        return Result.success("删除成功", null);
    }
}
