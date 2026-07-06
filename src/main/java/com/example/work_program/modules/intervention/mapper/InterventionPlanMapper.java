package com.example.work_program.modules.intervention.mapper;

import com.example.work_program.modules.intervention.entity.InterventionPlan;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InterventionPlanMapper {
    @Select("<script>SELECT * FROM intervention_plan <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "</where> ORDER BY create_time DESC LIMIT #{offset}, #{limit}</script>")
    List<InterventionPlan> findAll(@Param("elderId") Long elderId, @Param("status") String status, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM intervention_plan <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='status != null and status != \"\"'>AND status = #{status}</if>" +
            "</where></script>")
    Long count(@Param("elderId") Long elderId, @Param("status") String status);

    @Select("SELECT * FROM intervention_plan WHERE id = #{id}")
    InterventionPlan findById(@Param("id") Long id);

    @Insert("INSERT INTO intervention_plan (id, elder_id, plan_name, plan_type, risk_level, " +
            "cognitive_training, lifestyle_intervention, rehabilitation_plan, goals, " +
            "start_date, end_date, responsible_doctor, status, remark, create_time, update_time) " +
            "VALUES (#{id}, #{elderId}, #{planName}, #{planType}, #{riskLevel}, " +
            "#{cognitiveTraining}, #{lifestyleIntervention}, #{rehabilitationPlan}, #{goals}, " +
            "#{startDate}, #{endDate}, #{responsibleDoctor}, #{status}, #{remark}, NOW(), NOW())")
    void insert(InterventionPlan plan);

    @Update("UPDATE intervention_plan SET elder_id = #{elderId}, plan_name = #{planName}, " +
            "plan_type = #{planType}, risk_level = #{riskLevel}, cognitive_training = #{cognitiveTraining}, " +
            "lifestyle_intervention = #{lifestyleIntervention}, rehabilitation_plan = #{rehabilitationPlan}, " +
            "goals = #{goals}, start_date = #{startDate}, end_date = #{endDate}, " +
            "responsible_doctor = #{responsibleDoctor}, status = #{status}, remark = #{remark}, " +
            "update_time = NOW() WHERE id = #{id}")
    void update(InterventionPlan plan);

    @Delete("DELETE FROM intervention_plan WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
