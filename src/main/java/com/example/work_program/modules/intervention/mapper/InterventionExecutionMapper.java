package com.example.work_program.modules.intervention.mapper;

import com.example.work_program.modules.intervention.entity.InterventionExecution;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InterventionExecutionMapper {
    @Select("<script>SELECT * FROM intervention_execution <where>" +
            "<if test='planId != null'>AND plan_id = #{planId}</if>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "</where> ORDER BY execution_date DESC LIMIT #{offset}, #{limit}</script>")
    List<InterventionExecution> findAll(@Param("planId") Long planId, @Param("elderId") Long elderId, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM intervention_execution <where>" +
            "<if test='planId != null'>AND plan_id = #{planId}</if>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "</where></script>")
    Long count(@Param("planId") Long planId, @Param("elderId") Long elderId);

    @Select("SELECT * FROM intervention_execution WHERE id = #{id}")
    InterventionExecution findById(@Param("id") Long id);

    @Insert("INSERT INTO intervention_execution (id, plan_id, elder_id, execution_type, content, " +
            "execution_date, duration, effect_evaluation, evaluator, remark, create_time, update_time) " +
            "VALUES (#{id}, #{planId}, #{elderId}, #{executionType}, #{content}, " +
            "#{executionDate}, #{duration}, #{effectEvaluation}, #{evaluator}, #{remark}, NOW(), NOW())")
    void insert(InterventionExecution execution);

    @Update("UPDATE intervention_execution SET plan_id = #{planId}, elder_id = #{elderId}, " +
            "execution_type = #{executionType}, content = #{content}, execution_date = #{executionDate}, " +
            "duration = #{duration}, effect_evaluation = #{effectEvaluation}, evaluator = #{evaluator}, " +
            "remark = #{remark}, update_time = NOW() WHERE id = #{id}")
    void update(InterventionExecution execution);

    @Delete("DELETE FROM intervention_execution WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
