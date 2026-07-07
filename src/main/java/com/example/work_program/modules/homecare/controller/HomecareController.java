package com.example.work_program.modules.homecare.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.annotation.LogOperation;
import com.example.work_program.common.PageResult;
import com.example.work_program.common.Result;
import com.example.work_program.modules.homecare.entity.*;
import com.example.work_program.modules.homecare.service.HomecareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/homecare")
@LoginRequired
public class HomecareController {

    @Autowired
    private HomecareService homecareService;

    // ==================== 随访计划接口 ====================

    @GetMapping("/visit-plan/list")
    public Result<PageResult<VisitPlan>> listVisitPlans(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(homecareService.findVisitPlans(elderId, doctorId, status, pageNum, pageSize));
    }

    @GetMapping("/visit-plan/{id}")
    public Result<VisitPlan> getVisitPlanById(@PathVariable Long id) {
        return Result.success(homecareService.findVisitPlanById(id));
    }

    @PostMapping("/visit-plan/add")
    @LogOperation("添加随访计划")
    public Result<Void> addVisitPlan(@Valid @RequestBody VisitPlan plan) {
        homecareService.addVisitPlan(plan);
        return Result.success("随访计划添加成功", null);
    }

    @PutMapping("/visit-plan/update")
    @LogOperation("更新随访计划")
    public Result<Void> updateVisitPlan(@Valid @RequestBody VisitPlan plan) {
        homecareService.updateVisitPlan(plan);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/visit-plan/delete/{id}")
    @LogOperation("删除随访计划")
    public Result<Void> deleteVisitPlan(@PathVariable Long id) {
        homecareService.deleteVisitPlan(id);
        return Result.success("删除成功", null);
    }

    // ==================== 随访记录接口 ====================

    @GetMapping("/visit-record/list")
    public Result<PageResult<VisitRecord>> listVisitRecords(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) Long planId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(homecareService.findVisitRecords(elderId, planId, pageNum, pageSize));
    }

    @PostMapping("/visit-record/add")
    @LogOperation("添加随访记录")
    public Result<Void> addVisitRecord(@Valid @RequestBody VisitRecord record) {
        homecareService.addVisitRecord(record);
        return Result.success("随访记录添加成功", null);
    }

    // ==================== 健康预警接口 ====================

    @GetMapping("/health-alert/list")
    public Result<PageResult<HealthAlert>> listHealthAlerts(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) Integer isRead,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(homecareService.findHealthAlerts(elderId, isRead, pageNum, pageSize));
    }

    @GetMapping("/health-alert/unread-count")
    public Result<Long> countUnreadAlerts() {
        return Result.success(homecareService.countUnreadAlerts());
    }

    @PutMapping("/health-alert/{id}/read")
    @LogOperation("标记预警已读")
    public Result<Void> markAlertAsRead(@PathVariable Long id) {
        homecareService.markAlertAsRead(id);
        return Result.success("已标记为已读", null);
    }

    // ==================== 站内消息接口 ====================

    @GetMapping("/message/list")
    public Result<PageResult<Message>> listMessages(
            @RequestParam(required = false) Long senderId,
            @RequestParam(required = false) Long receiverId,
            @RequestParam(required = false) Integer isRead,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(homecareService.findMessages(senderId, receiverId, isRead, pageNum, pageSize));
    }

    @GetMapping("/message/unread-count")
    public Result<Long> countUnreadMessages(@RequestParam Long receiverId) {
        return Result.success(homecareService.countUnreadMessages(receiverId));
    }

    @PutMapping("/message/{id}/read")
    @LogOperation("标记消息已读")
    public Result<Void> markMessageAsRead(@PathVariable Long id) {
        homecareService.markMessageAsRead(id);
        return Result.success("已标记为已读", null);
    }

    @PostMapping("/message/send")
    @LogOperation("发送消息")
    public Result<Void> sendMessage(@Valid @RequestBody Message message) {
        homecareService.sendMessage(message);
        return Result.success("消息发送成功", null);
    }
}