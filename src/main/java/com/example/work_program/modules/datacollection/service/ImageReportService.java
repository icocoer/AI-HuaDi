package com.example.work_program.modules.datacollection.service;

import com.example.work_program.common.PageResult;
import com.example.work_program.modules.datacollection.entity.ImageReport;

import java.util.List;

public interface ImageReportService {
    ImageReport findById(Long id);
    List<ImageReport> findByElderId(Long elderId);
    List<ImageReport> findByCollectionId(Long collectionId);
    PageResult<ImageReport> findAll(Long elderId, String imageType, int pageNum, int pageSize);
    void add(ImageReport report);
    void update(ImageReport report);
    void deleteById(Long id);
    Long countByElderId(Long elderId);
}
