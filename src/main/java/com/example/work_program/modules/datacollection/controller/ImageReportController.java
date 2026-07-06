package com.example.work_program.modules.datacollection.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.common.Result;
import com.example.work_program.modules.datacollection.entity.ImageReport;
import com.example.work_program.modules.datacollection.service.ImageReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 影像报告管理控制器
 */
@RestController
@RequestMapping("/api/data/image")
@LoginRequired
public class ImageReportController {

    @Autowired
    private ImageReportService imageReportService;

    /**
     * 分页查询影像报告
     */
    @GetMapping("/list")
    public Result<List<ImageReport>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String imageType) {
        return Result.success(imageReportService.findAll(elderId, imageType));
    }

    /**
     * 获取影像报告详情
     */
    @GetMapping("/{id}")
    public Result<ImageReport> getById(@PathVariable Long id) {
        return Result.success(imageReportService.findById(id));
    }

    /**
     * 根据老人ID查询影像报告列表
     */
    @GetMapping("/elder/{elderId}")
    public Result<List<ImageReport>> getByElderId(@PathVariable Long elderId) {
        return Result.success(imageReportService.findByElderId(elderId));
    }

    /**
     * 添加影像报告
     */
    @PostMapping("/add")
    @LoginRequired(roles = {"admin", "doctor", "nurse"})
    public Result<Void> add(@RequestBody ImageReport report) {
        imageReportService.add(report);
        return Result.success("影像报告添加成功", null);
    }

    /**
     * 更新影像报告
     */
    @PutMapping("/update")
    @LoginRequired(roles = {"admin", "doctor"})
    public Result<Void> update(@RequestBody ImageReport report) {
        imageReportService.update(report);
        return Result.success("更新成功", null);
    }

    /**
     * 删除影像报告
     */
    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin", "doctor"})
    public Result<Void> delete(@PathVariable Long id) {
        imageReportService.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 获取老人影像报告数量
     */
    @GetMapping("/count/{elderId}")
    public Result<Long> countByElderId(@PathVariable Long elderId) {
        return Result.success(imageReportService.countByElderId(elderId));
    }
}
