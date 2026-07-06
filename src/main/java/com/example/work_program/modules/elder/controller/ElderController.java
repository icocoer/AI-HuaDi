package com.example.work_program.modules.elder.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.common.Result;
import com.example.work_program.modules.risk.entity.CognitiveAssessment;
import com.example.work_program.modules.elder.entity.ElderHealthRecord;
import com.example.work_program.modules.risk.service.CognitiveAssessmentService;
import com.example.work_program.modules.elder.service.ElderHealthRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/elder")
@LoginRequired
public class ElderController {

    @Autowired
    private ElderHealthRecordService elderHealthRecordService;

    @Autowired
    private CognitiveAssessmentService cognitiveAssessmentService;

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        List<ElderHealthRecord> all = elderHealthRecordService.findAll(null, null);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", all.size());
        stats.put("lowCount", all.stream().filter(e -> "low".equals(e.getRiskLevel())).count());
        stats.put("mediumCount", all.stream().filter(e -> "medium".equals(e.getRiskLevel())).count());
        stats.put("highCount", all.stream().filter(e -> "high".equals(e.getRiskLevel())).count());

        Map<String, Long> ageDistribution = new HashMap<>();
        ageDistribution.put("60-69", all.stream().filter(e -> {
            int age = java.time.LocalDate.now().getYear() - e.getBirthDate().getYear();
            return e.getBirthDate() != null && age >= 60 && age < 70;
        }).count());
        ageDistribution.put("70-79", all.stream().filter(e -> {
            int age = java.time.LocalDate.now().getYear() - e.getBirthDate().getYear();
            return e.getBirthDate() != null && age >= 70 && age < 80;
        }).count());
        ageDistribution.put("80+", all.stream().filter(e -> {
            int age = java.time.LocalDate.now().getYear() - e.getBirthDate().getYear();
            return e.getBirthDate() != null && age >= 80;
        }).count());
        stats.put("ageDistribution", ageDistribution);

        return Result.success(stats);
    }

    @GetMapping("/{id}/full-profile")
    public Result<Map<String, Object>> getFullProfile(@PathVariable Long id) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("record", elderHealthRecordService.findById(id));
        List<CognitiveAssessment> assessments = cognitiveAssessmentService.findAll(id, null);
        profile.put("assessments", assessments);
        profile.put("latestAssessment", assessments.isEmpty() ? null : assessments.get(0));
        return Result.success(profile);
    }
}
