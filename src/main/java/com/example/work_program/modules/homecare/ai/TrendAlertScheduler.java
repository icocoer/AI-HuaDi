package com.example.work_program.modules.homecare.ai;

import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import com.example.work_program.modules.datacollection.mapper.HealthDataCollectionMapper;
import com.example.work_program.modules.elder.mapper.ElderHealthRecordMapper;
import com.example.work_program.modules.elder.entity.ElderHealthRecord;
import com.example.work_program.modules.homecare.entity.HealthAlert;
import com.example.work_program.modules.homecare.entity.Message;
import com.example.work_program.modules.homecare.mapper.HealthAlertMapper;
import com.example.work_program.modules.homecare.mapper.MessageMapper;
import com.example.work_program.modules.system.entity.User;
import com.example.work_program.modules.system.mapper.UserMapper;
import com.example.work_program.util.SnowflakeIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * AI智能健康趋势预警调度器
 *
 * 每日定时执行：
 * 1. 遍历所有在册老人
 * 2. 采集最近N天的健康数据
 * 3. 调用AI分析引擎进行趋势检测
 * 4. 发现异常自动生成预警并通知医生
 */
@Component
public class TrendAlertScheduler {

    private static final Logger log = LoggerFactory.getLogger(TrendAlertScheduler.class);

    /** 分析窗口：最近几天的数据 */
    private static final int ANALYSIS_DAYS = 7;

    @Autowired
    private AiHealthAnalyzer aiHealthAnalyzer;

    @Autowired
    private HealthDataCollectionMapper healthDataCollectionMapper;

    @Autowired
    private ElderHealthRecordMapper elderHealthRecordMapper;

    @Autowired
    private HealthAlertMapper healthAlertMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SnowflakeIdGenerator idGenerator;

    /**
     * 每日凌晨2点执行健康趋势分析
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void executeTrendAnalysis() {
        log.info("[AI预警] 开始执行每日健康趋势分析...");

        // 1. 获取所有在册老人
        List<ElderHealthRecord> elders = elderHealthRecordMapper.findAll(null, null, 0, 10000);
        if (elders.isEmpty()) {
            log.info("[AI预警] 无在册老人，跳过分析");
            return;
        }

        int alertCount = 0;

        for (ElderHealthRecord elder : elders) {
            try {
                // 2. 获取该老人最近N天的健康数据
                List<HealthDataCollection> recentData = healthDataCollectionMapper
                    .findRecentByElderId(elder.getId(), ANALYSIS_DAYS);

                if (recentData.isEmpty()) continue;

                // 3. 调用AI分析引擎
                AiHealthAnalyzer.AnalysisResult result = aiHealthAnalyzer.analyze(recentData);

                // 4. 如果检测到异常，生成预警
                if (!"normal".equals(result.getRiskLevel())) {
                    createAlert(elder, result);
                    alertCount++;
                }
            } catch (Exception e) {
                log.error("[AI预警] 分析老人 {} 数据时出错: {}", elder.getId(), e.getMessage());
            }
        }

        log.info("[AI预警] 分析完成，共处理 {} 位老人，生成 {} 条预警", elders.size(), alertCount);
    }

    /**
     * 生成预警记录并通知医生
     */
    private void createAlert(ElderHealthRecord elder, AiHealthAnalyzer.AnalysisResult result) {
        String alertLevel = "danger".equals(result.getRiskLevel()) ? "danger" : "warning";
        String alertType = result.getAlerts().isEmpty() ? "trend_anomaly" : "threshold_exceeded";

        // 构建预警消息
        String message = String.format(
            "[AI智能预警] 老人%s的健康数据检测到%s。%s（风险概率：%.0f%%）",
            elder.getName(),
            "danger".equals(alertLevel) ? "高风险异常" : "趋势异常",
            result.getSummary(),
            result.getRiskScore() * 100
        );

        // 1. 写入健康预警表
        HealthAlert alert = new HealthAlert();
        alert.setId(idGenerator.nextId());
        alert.setElderId(elder.getId());
        alert.setAlertType(alertType);
        alert.setAlertLevel(alertLevel);
        alert.setAlertMessage(message);
        alert.setIsRead(0);
        healthAlertMapper.insert(alert);

        // 2. 发送站内消息给所有医生
        User doctorQuery = new User();
        doctorQuery.setRole("doctor");
        List<User> doctors = userMapper.findDoctors();
        for (User doctor : doctors) {
            Message msg = new Message();
            msg.setId(idGenerator.nextId());
            msg.setSenderId(0L); // 系统发送
            msg.setReceiverId(doctor.getId());
            msg.setMessageType("system");
            msg.setContent(message);
            msg.setIsRead(0);
            messageMapper.insert(msg);
        }

        log.info("[AI预警] 已为老人 {} 生成{}预警: {}", elder.getName(), alertLevel, result.getSummary());
    }
}
