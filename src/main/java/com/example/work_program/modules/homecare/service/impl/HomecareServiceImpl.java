package com.example.work_program.modules.homecare.service.impl;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.homecare.entity.*;
import com.example.work_program.modules.homecare.mapper.*;
import com.example.work_program.modules.homecare.service.HomecareService;
import com.example.work_program.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class HomecareServiceImpl implements HomecareService {

    @Autowired
    private VisitPlanMapper visitPlanMapper;
    @Autowired
    private VisitRecordMapper visitRecordMapper;
    @Autowired
    private HealthAlertMapper healthAlertMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private SnowflakeIdGenerator idGenerator;

    @Override
    public PageResult<VisitPlan> findVisitPlans(Long elderId, Long doctorId, String status, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = visitPlanMapper.count(elderId, doctorId, status);
        if (total == 0) return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        List<VisitPlan> list = visitPlanMapper.findAll(elderId, doctorId, status, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public VisitPlan findVisitPlanById(Long id) {
        return visitPlanMapper.findById(id);
    }

    @Override
    public void addVisitPlan(VisitPlan plan) {
        plan.setId(idGenerator.nextId());
        visitPlanMapper.insert(plan);
    }

    @Override
    public void updateVisitPlan(VisitPlan plan) {
        visitPlanMapper.update(plan);
    }

    @Override
    public void deleteVisitPlan(Long id) {
        visitPlanMapper.deleteById(id);
    }

    @Override
    public PageResult<VisitRecord> findVisitRecords(Long elderId, Long planId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = visitRecordMapper.count(elderId, planId);
        if (total == 0) return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        List<VisitRecord> list = visitRecordMapper.findAll(elderId, planId, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public void addVisitRecord(VisitRecord record) {
        record.setId(idGenerator.nextId());
        visitRecordMapper.insert(record);
    }

    @Override
    public PageResult<HealthAlert> findHealthAlerts(Long elderId, Integer isRead, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = healthAlertMapper.count(elderId, isRead);
        if (total == 0) return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        List<HealthAlert> list = healthAlertMapper.findAll(elderId, isRead, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public Long countUnreadAlerts() {
        return healthAlertMapper.countUnread();
    }

    @Override
    public void markAlertAsRead(Long id) {
        healthAlertMapper.markAsRead(id);
    }

    @Override
    public PageResult<Message> findMessages(Long senderId, Long receiverId, Integer isRead, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = messageMapper.count(senderId, receiverId, isRead);
        if (total == 0) return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        List<Message> list = messageMapper.findAll(senderId, receiverId, isRead, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public Long countUnreadMessages(Long receiverId) {
        return messageMapper.countUnread(receiverId);
    }

    @Override
    public void markMessageAsRead(Long id) {
        messageMapper.markAsRead(id);
    }

    @Override
    public void sendMessage(Message message) {
        message.setId(idGenerator.nextId());
        message.setIsRead(0);
        messageMapper.insert(message);
    }
}