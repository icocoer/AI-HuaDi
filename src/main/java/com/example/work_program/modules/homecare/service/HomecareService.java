package com.example.work_program.modules.homecare.service;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.homecare.entity.*;

public interface HomecareService {
    // 随访计划
    PageResult<VisitPlan> findVisitPlans(Long elderId, Long doctorId, String status, int pageNum, int pageSize);
    VisitPlan findVisitPlanById(Long id);
    void addVisitPlan(VisitPlan plan);
    void updateVisitPlan(VisitPlan plan);
    void deleteVisitPlan(Long id);

    // 随访记录
    PageResult<VisitRecord> findVisitRecords(Long elderId, Long planId, int pageNum, int pageSize);
    void addVisitRecord(VisitRecord record);

    // 健康预警
    PageResult<HealthAlert> findHealthAlerts(Long elderId, Integer isRead, int pageNum, int pageSize);
    Long countUnreadAlerts();
    void markAlertAsRead(Long id);

    // 站内消息
    PageResult<Message> findMessages(Long senderId, Long receiverId, Integer isRead, int pageNum, int pageSize);
    Long countUnreadMessages(Long receiverId);
    void markMessageAsRead(Long id);
    void sendMessage(Message message);
}