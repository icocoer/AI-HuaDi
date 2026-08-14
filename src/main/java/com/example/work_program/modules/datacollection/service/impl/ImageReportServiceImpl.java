package com.example.work_program.modules.datacollection.service.impl;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import com.example.work_program.modules.datacollection.entity.ImageReport;
import com.example.work_program.modules.datacollection.mapper.HealthDataCollectionMapper;
import com.example.work_program.modules.datacollection.mapper.ImageReportMapper;
import com.example.work_program.modules.datacollection.service.ImageReportService;
import com.example.work_program.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class ImageReportServiceImpl implements ImageReportService {

    @Autowired
    private ImageReportMapper imageReportMapper;

    @Autowired
    private HealthDataCollectionMapper healthDataCollectionMapper;

    @Autowired
    private SnowflakeIdGenerator idGenerator;

    @Override
    public ImageReport findById(Long id) {
        return imageReportMapper.findById(id);
    }

    @Override
    public List<ImageReport> findByElderId(Long elderId) {
        return imageReportMapper.findByElderId(elderId);
    }

    @Override
    public List<ImageReport> findByCollectionId(Long collectionId) {
        return imageReportMapper.findByCollectionId(collectionId);
    }

    @Override
    public PageResult<ImageReport> findAll(Long elderId, String imageType, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        Long total = imageReportMapper.count(elderId, imageType);
        if (total == 0) {
            return new PageResult<>(Collections.emptyList(), 0L, pageNum, pageSize);
        }
        List<ImageReport> list = imageReportMapper.findAll(elderId, imageType, offset, pageSize);
        return new PageResult<>(list, total, pageNum, pageSize);
    }

    @Override
    @Transactional
    public void add(ImageReport report) {
        report.setId(idGenerator.nextId());
        imageReportMapper.insert(report);

        // 同时写入 health_data_collection 表
        HealthDataCollection collection = new HealthDataCollection();
        collection.setId(idGenerator.nextId());
        collection.setElderId(report.getElderId());
        collection.setDataSource("image");
        collection.setDataType(report.getImageType());
        collection.setDataContent(report.getDiagnosisResult());
        collection.setCollector(report.getDoctorName());
        collection.setCollectionDate(report.getDiagnosisDate());
        healthDataCollectionMapper.insert(collection);
    }

    @Override
    public void update(ImageReport report) {
        imageReportMapper.update(report);
    }

    @Override
    public void deleteById(Long id) {
        imageReportMapper.deleteById(id);
    }

    @Override
    public Long countByElderId(Long elderId) {
        return imageReportMapper.countByElderId(elderId);
    }
}
