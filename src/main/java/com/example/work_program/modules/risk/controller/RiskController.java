package com.example.work_program.modules.risk.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.common.PageResult;
import com.example.work_program.common.Result;
import com.example.work_program.modules.risk.entity.CognitiveAssessment;
import com.example.work_program.modules.risk.entity.RiskWarning;
import com.example.work_program.modules.risk.service.CognitiveAssessmentService;
import com.example.work_program.modules.risk.service.RiskWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/risk")
@LoginRequired
public class RiskController {

    @Autowired
    private CognitiveAssessmentService cognitiveAssessmentService;

    @Autowired
    private RiskWarningService riskWarningService;

    @GetMapping("/assessment/list")
    public Result<PageResult<CognitiveAssessment>> assessmentList(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(cognitiveAssessmentService.findAll(elderId, riskLevel, pageNum, pageSize));
    }

    @GetMapping("/assess/{elderId}")
    public Result<Map<String, Object>> assessRisk(@PathVariable Long elderId) {
        var assessmentPage = cognitiveAssessmentService.findAll(elderId, null, 1, 10000);
        List<CognitiveAssessment> assessments = assessmentPage.getList();
        CognitiveAssessment latest = cognitiveAssessmentService.findLatestByElderId(elderId);

        Map<String, Object> result = new HashMap<>();
        if (latest != null) {
            result.put("currentLevel", latest.getRiskLevel());
            result.put("totalScore", latest.getTotalScore());
            result.put("latestAssessment", latest);
            result.put("assessmentCount", assessments.size());

            if (assessments.size() >= 2) {
                int prev = assessments.get(1).getTotalScore();
                int curr = latest.getTotalScore();
                if (curr < prev) {
                    result.put("trend", "declining");
                    result.put("trendMsg", "认知功能呈下降趋势，需要关注");
                } else if (curr > prev) {
                    result.put("trend", "improving");
                    result.put("trendMsg", "认知功能有所改善");
                } else {
                    result.put("trend", "stable");
                    result.put("trendMsg", "认知功能保持稳定");
                }
            } else {
                result.put("trend", "baseline");
                result.put("trendMsg", "已建立基线评估");
            }

            if ("high".equals(latest.getRiskLevel()) || "severe".equals(latest.getRiskLevel())) {
                riskWarningService.checkAndCreateWarning(elderId, latest.getRiskLevel(), latest.getAssessmentResult());
            }
        } else {
            result.put("currentLevel", "unknown");
            result.put("trend", "no_data");
            result.put("trendMsg", "暂无评估数据");
        }

        return Result.success(result);
    }

    @GetMapping("/warnings")
    public Result<List<RiskWarning>> getWarnings(@RequestParam(required = false, defaultValue = "false") Boolean unreadOnly) {
        if (unreadOnly) {
            return Result.success(riskWarningService.findUnread());
        }
        return Result.success(riskWarningService.findAll());
    }

    @PutMapping("/warnings/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        riskWarningService.markAsRead(id);
        return Result.success("已标记为已读", null);
    }

    @GetMapping("/warnings/unread-count")
    public Result<Long> getUnreadCount() {
        return Result.success(riskWarningService.countUnread());
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getRiskStatistics() {
        Map<String, Object> stats = new HashMap<>();
        var assessmentPage = cognitiveAssessmentService.findAll(null, null, 1, 10000);
        List<CognitiveAssessment> all = assessmentPage.getList();
        stats.put("totalAssessments", all.size());
        stats.put("lowCount", all.stream().filter(a -> "low".equals(a.getRiskLevel())).count());
        stats.put("mediumCount", all.stream().filter(a -> "medium".equals(a.getRiskLevel())).count());
        stats.put("highCount", all.stream().filter(a -> "high".equals(a.getRiskLevel())).count());
        stats.put("unreadWarnings", riskWarningService.countUnread());
        return Result.success(stats);
    }
}
