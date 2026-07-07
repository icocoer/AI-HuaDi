package com.example.work_program.modules.homecare.mapper;

import com.example.work_program.modules.homecare.entity.VisitRecord;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VisitRecordMapper {
    @Select("<script>SELECT * FROM homecare_visit_record <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='planId != null'>AND plan_id = #{planId}</if>" +
            "</where> ORDER BY visit_date DESC LIMIT #{offset}, #{limit}</script>")
    List<VisitRecord> findAll(@Param("elderId") Long elderId, @Param("planId") Long planId,
                             @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM homecare_visit_record <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='planId != null'>AND plan_id = #{planId}</if>" +
            "</where></script>")
    Long count(@Param("elderId") Long elderId, @Param("planId") Long planId);

    @Select("SELECT * FROM homecare_visit_record WHERE id = #{id}")
    VisitRecord findById(@Param("id") Long id);

    @Insert("INSERT INTO homecare_visit_record (id, plan_id, elder_id, visit_date, visit_content, health_status, recommendations, next_plan_date, create_time) " +
            "VALUES (#{id}, #{planId}, #{elderId}, #{visitDate}, #{visitContent}, #{healthStatus}, #{recommendations}, #{nextPlanDate}, NOW())")
    void insert(VisitRecord record);

    @Delete("DELETE FROM homecare_visit_record WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}