package com.example.work_program.modules.risk.mapper;

import com.example.work_program.modules.risk.entity.CognitiveAssessment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CognitiveAssessmentMapper {
    @Select("<script>SELECT * FROM cognitive_assessment <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='riskLevel != null and riskLevel != \"\"'>AND risk_level = #{riskLevel}</if>" +
            "</where> ORDER BY assessment_time DESC LIMIT #{offset}, #{limit}</script>")
    List<CognitiveAssessment> findAll(@Param("elderId") Long elderId, @Param("riskLevel") String riskLevel, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM cognitive_assessment <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='riskLevel != null and riskLevel != \"\"'>AND risk_level = #{riskLevel}</if>" +
            "</where></script>")
    Long count(@Param("elderId") Long elderId, @Param("riskLevel") String riskLevel);

    @Select("SELECT * FROM cognitive_assessment WHERE id = #{id}")
    CognitiveAssessment findById(@Param("id") Long id);

    @Select("SELECT * FROM cognitive_assessment WHERE elder_id = #{elderId} ORDER BY assessment_time DESC LIMIT 1")
    CognitiveAssessment findLatestByElderId(@Param("elderId") Long elderId);

    @Insert("INSERT INTO cognitive_assessment (id, elder_id, assessment_type, total_score, risk_level, " +
            "assessment_result, recommendations, assessor, assessment_place, assessment_time, " +
            "next_assessment_date, remark, create_time, update_time) " +
            "VALUES (#{id}, #{elderId}, #{assessmentType}, #{totalScore}, #{riskLevel}, " +
            "#{assessmentResult}, #{recommendations}, #{assessor}, #{assessmentPlace}, #{assessmentTime}, " +
            "#{nextAssessmentDate}, #{remark}, NOW(), NOW())")
    void insert(CognitiveAssessment assessment);

    @Update("UPDATE cognitive_assessment SET elder_id = #{elderId}, assessment_type = #{assessmentType}, " +
            "total_score = #{totalScore}, risk_level = #{riskLevel}, assessment_result = #{assessmentResult}, " +
            "recommendations = #{recommendations}, assessor = #{assessor}, assessment_place = #{assessmentPlace}, " +
            "assessment_time = #{assessmentTime}, next_assessment_date = #{nextAssessmentDate}, " +
            "remark = #{remark}, update_time = NOW() WHERE id = #{id}")
    void update(CognitiveAssessment assessment);

    @Delete("DELETE FROM cognitive_assessment WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
