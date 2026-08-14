package com.example.work_program.modules.datacollection.service.impl;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.datacollection.dto.DataCollectionStatisticsDTO;
import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import com.example.work_program.modules.datacollection.entity.HealthQuestionnaire;
import com.example.work_program.modules.datacollection.entity.ImageReport;
import com.example.work_program.modules.datacollection.entity.SmartAssessment;
import com.example.work_program.modules.datacollection.mapper.*;
import com.example.work_program.modules.datacollection.service.HealthDataCollectionService;
import com.example.work_program.modules.elder.mapper.ElderHealthRecordMapper;
import com.example.work_program.modules.elder.entity.ElderHealthRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HealthDataCollectionServiceImpl implements HealthDataCollectionService {

    @Autowired
    private HealthDataCollectionMapper healthDataCollectionMapper;
    @Autowired
    private SmartAssessmentMapper smartAssessmentMapper;
    @Autowired
    private HealthQuestionnaireMapper healthQuestionnaireMapper;
    @Autowired
    private ImageReportMapper imageReportMapper;
    @Autowired
    private ElderHealthRecordMapper elderHealthRecordMapper;

    @Override
    public PageResult<HealthDataCollection> findAll(Long elderId, String dataSource, int pageNum, int pageSize) {
        // 如果指定了数据来源，从对应子表查询
        if (dataSource != null && !dataSource.isEmpty()) {
            return findBySource(elderId, dataSource, pageNum, pageSize);
        }

        // 否则从三个子表合并查询
        List<HealthDataCollection> allRecords = new ArrayList<>();

        // 查询智能评估（如果没有指定数据来源，或者来源是smart）
        List<SmartAssessment> smartList = smartAssessmentMapper.findAll(elderId, null, 0, 10000);
        for (SmartAssessment sa : smartList) {
            HealthDataCollection record = new HealthDataCollection();
            record.setId(sa.getId());
            record.setElderId(sa.getElderId());
            record.setDataSource("smart");
            record.setDataType(sa.getAssessmentType());
            record.setDataContent(sa.getAssessmentResult());
            record.setCollector(sa.getAssessor());
            record.setCollectionDate(sa.getAssessmentTime() != null ? sa.getAssessmentTime().toLocalDate() : null);
            record.setCreateTime(sa.getCreateTime());
            allRecords.add(record);
        }

        // 查询健康问询
        List<HealthQuestionnaire> questionnaireList = healthQuestionnaireMapper.findAll(elderId, null, 0, 10000);
        for (HealthQuestionnaire hq : questionnaireList) {
            HealthDataCollection record = new HealthDataCollection();
            record.setId(hq.getId());
            record.setElderId(hq.getElderId());
            record.setDataSource("questionnaire");
            record.setDataType(hq.getQuestionnaireType());
            record.setDataContent(hq.getSummary());
            record.setCollector(hq.getSurveyor());
            record.setCollectionDate(hq.getSurveyTime() != null ? hq.getSurveyTime().toLocalDate() : null);
            record.setCreateTime(hq.getCreateTime());
            allRecords.add(record);
        }

        // 查询影像报告
        List<ImageReport> imageList = imageReportMapper.findAll(elderId, null, 0, 10000);
        for (ImageReport ir : imageList) {
            HealthDataCollection record = new HealthDataCollection();
            record.setId(ir.getId());
            record.setElderId(ir.getElderId());
            record.setDataSource("image");
            record.setDataType(ir.getImageType());
            record.setDataContent(ir.getDiagnosisResult());
            record.setCollector(ir.getDoctorName());
            record.setCollectionDate(ir.getDiagnosisDate());
            record.setCreateTime(ir.getUploadTime());
            allRecords.add(record);
        }

        // 填充老人姓名
        Map<Long, String> elderNameMap = new HashMap<>();
        for (HealthDataCollection record : allRecords) {
            if (!elderNameMap.containsKey(record.getElderId())) {
                ElderHealthRecord elder = elderHealthRecordMapper.findById(record.getElderId());
                elderNameMap.put(record.getElderId(), elder != null ? elder.getName() : String.valueOf(record.getElderId()));
            }
            record.setElderName(elderNameMap.get(record.getElderId()));
        }

        // 按创建时间倒序排序
        allRecords.sort((a, b) -> {
            if (a.getCreateTime() == null) return 1;
            if (b.getCreateTime() == null) return -1;
            return b.getCreateTime().compareTo(a.getCreateTime());
        });

        // 分页
        int total = allRecords.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        List<HealthDataCollection> pageList = start < total ? allRecords.subList(start, end) : Collections.emptyList();

        return new PageResult<>(pageList, (long) total, pageNum, pageSize);
    }

    private PageResult<HealthDataCollection> findBySource(Long elderId, String dataSource, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<HealthDataCollection> list = new ArrayList<>();
        long total = 0;

        if ("smart".equals(dataSource)) {
            total = smartAssessmentMapper.count(elderId, null);
            if (total > 0) {
                List<SmartAssessment> smartList = smartAssessmentMapper.findAll(elderId, null, offset, pageSize);
                for (SmartAssessment sa : smartList) {
                    HealthDataCollection record = new HealthDataCollection();
                    record.setId(sa.getId());
                    record.setElderId(sa.getElderId());
                    record.setDataSource("smart");
                    record.setDataType(sa.getAssessmentType());
                    record.setDataContent(sa.getAssessmentResult());
                    record.setCollector(sa.getAssessor());
                    record.setCollectionDate(sa.getAssessmentTime() != null ? sa.getAssessmentTime().toLocalDate() : null);
                    record.setCreateTime(sa.getCreateTime());
                    list.add(record);
                }
            }
        } else if ("questionnaire".equals(dataSource)) {
            total = healthQuestionnaireMapper.count(elderId, null);
            if (total > 0) {
                List<HealthQuestionnaire> questionnaireList = healthQuestionnaireMapper.findAll(elderId, null, offset, pageSize);
                for (HealthQuestionnaire hq : questionnaireList) {
                    HealthDataCollection record = new HealthDataCollection();
                    record.setId(hq.getId());
                    record.setElderId(hq.getElderId());
                    record.setDataSource("questionnaire");
                    record.setDataType(hq.getQuestionnaireType());
                    record.setDataContent(hq.getSummary());
                    record.setCollector(hq.getSurveyor());
                    record.setCollectionDate(hq.getSurveyTime() != null ? hq.getSurveyTime().toLocalDate() : null);
                    record.setCreateTime(hq.getCreateTime());
                    list.add(record);
                }
            }
        } else if ("image".equals(dataSource)) {
            total = imageReportMapper.count(elderId, null);
            if (total > 0) {
                List<ImageReport> imageList = imageReportMapper.findAll(elderId, null, offset, pageSize);
                for (ImageReport ir : imageList) {
                    HealthDataCollection record = new HealthDataCollection();
                    record.setId(ir.getId());
                    record.setElderId(ir.getElderId());
                    record.setDataSource("image");
                    record.setDataType(ir.getImageType());
                    record.setDataContent(ir.getDiagnosisResult());
                    record.setCollector(ir.getDoctorName());
                    record.setCollectionDate(ir.getDiagnosisDate());
                    record.setCreateTime(ir.getUploadTime());
                    list.add(record);
                }
            }
        }

        // 填充老人姓名
        for (HealthDataCollection record : list) {
            ElderHealthRecord elder = elderHealthRecordMapper.findById(record.getElderId());
            record.setElderName(elder != null ? elder.getName() : String.valueOf(record.getElderId()));
        }

        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    public HealthDataCollection findById(Long id) {
        return healthDataCollectionMapper.findById(id);
    }

    @Override
    public void add(HealthDataCollection data) {
        healthDataCollectionMapper.insert(data);
    }

    @Override
    public void update(HealthDataCollection data) {
        healthDataCollectionMapper.update(data);
    }

    @Override
    public void deleteById(Long id) {
        healthDataCollectionMapper.deleteById(id);
    }

    @Override
    public DataCollectionStatisticsDTO getStatistics(Long elderId) {
        DataCollectionStatisticsDTO stats = new DataCollectionStatisticsDTO();

        long smartCount = smartAssessmentMapper.count(elderId, null);
        long questionnaireCount = healthQuestionnaireMapper.count(elderId, null);
        long imageCount = imageReportMapper.count(elderId, null);

        stats.setSmartCount(smartCount);
        stats.setQuestionnaireCount(questionnaireCount);
        stats.setImageCount(imageCount);
        stats.setTotalCount(smartCount + questionnaireCount + imageCount);

        return stats;
    }
}
