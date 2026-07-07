package com.example.work_program.modules.homecare.mapper;

import com.example.work_program.modules.homecare.entity.VisitPlan;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VisitPlanMapper {
    @Select("<script>SELECT * FROM homecare_visit_plan <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='doctorId != null'>AND doctor_id = #{doctorId}</if>" +
            "<if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "</where> ORDER BY planned_date DESC LIMIT #{offset}, #{limit}</script>")
    List<VisitPlan> findAll(@Param("elderId") Long elderId, @Param("doctorId") Long doctorId,
                           @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM homecare_visit_plan <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='doctorId != null'>AND doctor_id = #{doctorId}</if>" +
            "<if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "</where></script>")
    Long count(@Param("elderId") Long elderId, @Param("doctorId") Long doctorId, @Param("status") String status);

    @Select("SELECT * FROM homecare_visit_plan WHERE id = #{id}")
    VisitPlan findById(@Param("id") Long id);

    @Insert("INSERT INTO homecare_visit_plan (id, elder_id, doctor_id, visit_type, planned_date, status, remark, create_time, update_time) " +
            "VALUES (#{id}, #{elderId}, #{doctorId}, #{visitType}, #{plannedDate}, #{status}, #{remark}, NOW(), NOW())")
    void insert(VisitPlan plan);

    @Update("UPDATE homecare_visit_plan SET elder_id=#{elderId}, doctor_id=#{doctorId}, visit_type=#{visitType}, " +
            "planned_date=#{plannedDate}, status=#{status}, remark=#{remark}, update_time=NOW() WHERE id=#{id}")
    void update(VisitPlan plan);

    @Delete("DELETE FROM homecare_visit_plan WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}