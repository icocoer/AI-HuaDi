package com.example.work_program.modules.datacollection.service.impl;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.datacollection.entity.ImageReport;
import com.example.work_program.modules.datacollection.mapper.ImageReportMapper;
import com.example.work_program.modules.datacollection.service.ImageReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ImageReportServiceImpl implements ImageReportService {

    @Autowired
    private ImageReportMapper imageReportMapper;

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
    public void add(ImageReport report) {
        imageReportMapper.insert(report);
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
