package com.example.work_program.modules.intervention.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.common.Result;
import com.example.work_program.modules.intervention.entity.InterventionExecution;
import com.example.work_program.modules.intervention.entity.InterventionPlan;
import com.example.work_program.modules.intervention.service.InterventionExecutionService;
import com.example.work_program.modules.intervention.service.InterventionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/intervention")
@LoginRequired
public class InterventionController {

    @Autowired
    private InterventionPlanService interventionPlanService;

    @Autowired
    private InterventionExecutionService interventionExecutionService;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        List<InterventionPlan> plans = interventionPlanService.findAll(null, null);
        List<InterventionExecution> executions = interventionExecutionService.findAll(null, null);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlans", plans.size());
        stats.put("pendingCount", plans.stream().filter(p -> "pending".equals(p.getStatus())).count());
        stats.put("inProgressCount", plans.stream().filter(p -> "in_progress".equals(p.getStatus())).count());
        stats.put("completedCount", plans.stream().filter(p -> "completed".equals(p.getStatus())).count());
        stats.put("totalExecutions", executions.size());

        long totalPlans = plans.size();
        double executionRate = totalPlans > 0 ? (double) executions.size() / totalPlans : 0.0;
        stats.put("executionRate", Math.round(executionRate * 100.0) / 100.0);

        return Result.success(stats);
    }

    @GetMapping("/elder/{elderId}/plans")
    public Result<Map<String, Object>> getElderPlans(@PathVariable Long elderId) {
        Map<String, Object> result = new HashMap<>();
        List<InterventionPlan> plans = interventionPlanService.findAll(elderId, null);
        result.put("plans", plans);
        if (!plans.isEmpty()) {
            List<InterventionExecution> executions = interventionExecutionService.findAll(plans.get(0).getId(), elderId);
            result.put("latestExecutions", executions);
        }
        return Result.success(result);
    }
}
