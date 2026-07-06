package com.example.work_program.modules.datacollection.controller;

import com.example.work_program.annotation.LoginRequired;
import com.example.work_program.annotation.LogOperation;
import com.example.work_program.common.PageResult;
import com.example.work_program.common.Result;
import com.example.work_program.modules.datacollection.dto.DataCollectionStatisticsDTO;
import com.example.work_program.modules.elder.entity.ElderHealthRecord;
import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import com.example.work_program.modules.datacollection.enums.DataSourceEnum;
import com.example.work_program.modules.datacollection.enums.DataTypeEnum;
import com.example.work_program.modules.elder.service.ElderHealthRecordService;
import com.example.work_program.modules.datacollection.service.HealthDataCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据影像采集系统 - 主控制器
 * 负责三种数据采集渠道：智能评估、健康问询、影像报告
 */
@RestController
@RequestMapping("/api/data/collection")
@LoginRequired
public class DataCollectionController {

    @Autowired
    private HealthDataCollectionService healthDataCollectionService;

    @Autowired
    private ElderHealthRecordService elderHealthRecordService;

    // ==================== 数据采集主记录接口 ====================

    /**
     * 分页查询数据采集记录
     */
    @GetMapping("/list")
    public Result<PageResult<HealthDataCollection>> list(
            @RequestParam(required = false) Long elderId,
            @RequestParam(required = false) String dataSource,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(healthDataCollectionService.findAll(elderId, dataSource, pageNum, pageSize));
    }

    /**
     * 获取采集记录详情
     */
    @GetMapping("/{id}")
    public Result<HealthDataCollection> getById(@PathVariable Long id) {
        return Result.success(healthDataCollectionService.findById(id));
    }

    /**
     * 添加数据采集记录
     */
    @PostMapping("/add")
    @LogOperation("添加采集记录")
    public Result<Void> add(@Valid @RequestBody HealthDataCollection data) {
        healthDataCollectionService.add(data);
        return Result.success("数据采集记录添加成功", null);
    }

    /**
     * 更新数据采集记录
     */
    @PutMapping("/update")
    @LogOperation("更新采集记录")
    public Result<Void> update(@Valid @RequestBody HealthDataCollection data) {
        healthDataCollectionService.update(data);
        return Result.success("更新成功", null);
    }

    /**
     * 删除数据采集记录
     */
    @DeleteMapping("/delete/{id}")
    @LoginRequired(roles = {"admin", "doctor"})
    @LogOperation("删除采集记录")
    public Result<Void> delete(@PathVariable Long id) {
        healthDataCollectionService.deleteById(id);
        return Result.success("删除成功", null);
    }

    // ==================== 数据统计接口 ====================

    /**
     * 获取采集统计数据
     */
    @GetMapping("/statistics")
    public Result<DataCollectionStatisticsDTO> getStatistics(@RequestParam(required = false) Long elderId) {
        DataCollectionStatisticsDTO stats = new DataCollectionStatisticsDTO();
        List<HealthDataCollection> allData = healthDataCollectionService.findAll(elderId, null, 1, 10000).getList();

        stats.setTotalCount((long) allData.size());
        stats.setSmartCount(allData.stream().filter(d -> "smart".equals(d.getDataSource())).count());
        stats.setQuestionnaireCount(allData.stream().filter(d -> "questionnaire".equals(d.getDataSource())).count());
        stats.setImageCount(allData.stream().filter(d -> "image".equals(d.getDataSource())).count());

        return Result.success(stats);
    }

    // ==================== 字典接口 ====================

    /**
     * 获取数据源枚举列表
     */
    @GetMapping("/datasource/enums")
    public Result<List<DataSourceEnum>> getDataSourceEnums() {
        return Result.success(java.util.Arrays.asList(DataSourceEnum.values()));
    }

    /**
     * 获取数据类型枚举列表
     */
    @GetMapping("/datatype/enums")
    public Result<List<DataTypeEnum>> getDataTypeEnums() {
        return Result.success(java.util.Arrays.asList(DataTypeEnum.values()));
    }
}
