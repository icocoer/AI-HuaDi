package com.example.work_program.modules.risk.mapper;

import com.example.work_program.modules.risk.entity.RiskWarning;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RiskWarningMapper {

    @Select("SELECT w.*, e.name as elder_name FROM risk_warning w " +
            "LEFT JOIN elder_health_record e ON w.elder_id = e.id " +
            "WHERE w.elder_id = #{elderId} ORDER BY w.create_time DESC")
    List<RiskWarning> findByElderId(Long elderId);

    @Select("SELECT w.*, e.name as elder_name FROM risk_warning w " +
            "LEFT JOIN elder_health_record e ON w.elder_id = e.id " +
            "WHERE w.is_read = 0 ORDER BY w.create_time DESC")
    List<RiskWarning> findUnread();

    @Select("SELECT w.*, e.name as elder_name FROM risk_warning w " +
            "LEFT JOIN elder_health_record e ON w.elder_id = e.id " +
            "ORDER BY w.create_time DESC")
    List<RiskWarning> findAll();

    @Select("SELECT w.*, e.name as elder_name FROM risk_warning w " +
            "LEFT JOIN elder_health_record e ON w.elder_id = e.id " +
            "WHERE w.id = #{id}")
    RiskWarning findById(Long id);

    @Insert("INSERT INTO risk_warning (id, elder_id, risk_level, warning_type, warning_msg, is_read, create_time) " +
            "VALUES (#{id}, #{elderId}, #{riskLevel}, #{warningType}, #{warningMsg}, 0, NOW())")
    int insert(RiskWarning warning);

    @Update("UPDATE risk_warning SET is_read = 1 WHERE id = #{id}")
    int markAsRead(Long id);

    @Select("SELECT COUNT(*) FROM risk_warning WHERE is_read = 0")
    Long countUnread();
}
