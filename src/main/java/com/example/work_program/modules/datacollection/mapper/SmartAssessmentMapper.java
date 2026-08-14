package com.example.work_program.modules.datacollection.mapper;

import com.example.work_program.modules.datacollection.entity.SmartAssessment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SmartAssessmentMapper {
    @Select("SELECT * FROM smart_assessment WHERE id = #{id}")
    SmartAssessment findById(@Param("id") Long id);

    @Select("SELECT * FROM smart_assessment WHERE elder_id = #{elderId} ORDER BY assessment_time DESC")
    List<SmartAssessment> findByElderId(@Param("elderId") Long elderId);

    @Select("SELECT * FROM smart_assessment WHERE collection_id = #{collectionId}")
    List<SmartAssessment> findByCollectionId(@Param("collectionId") Long collectionId);

    @Select("<script>SELECT * FROM smart_assessment <where>" +
            "<if test='elderId != null'> AND elder_id = #{elderId}</if>" +
            "<if test='assessmentType != null and assessmentType != \"\"'> AND assessment_type = #{assessmentType}</if>" +
            "</where> ORDER BY assessment_time DESC LIMIT #{offset}, #{limit}</script>")
    List<SmartAssessment> findAll(@Param("elderId") Long elderId, @Param("assessmentType") String assessmentType, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM smart_assessment <where>" +
            "<if test='elderId != null'> AND elder_id = #{elderId}</if>" +
            "<if test='assessmentType != null and assessmentType != \"\"'> AND assessment_type = #{assessmentType}</if>" +
            "</where></script>")
    Long count(@Param("elderId") Long elderId, @Param("assessmentType") String assessmentType);

    @Insert("INSERT INTO smart_assessment (id, collection_id, elder_id, assessment_type, assessment_items, " +
            "total_score, score_level, assessment_result, recommendations, assessor, " +
            "assessment_device, assessment_time, create_time) " +
            "VALUES (#{id}, #{collectionId}, #{elderId}, #{assessmentType}, #{assessmentItems}, " +
            "#{totalScore}, #{scoreLevel}, #{assessmentResult}, #{recommendations}, #{assessor}, " +
            "#{assessmentDevice}, #{assessmentTime}, NOW())")
    void insert(SmartAssessment assessment);

    @Update("UPDATE smart_assessment SET assessment_type = #{assessmentType}, " +
            "assessment_items = #{assessmentItems}, total_score = #{totalScore}, " +
            "score_level = #{scoreLevel}, assessment_result = #{assessmentResult}, " +
            "recommendations = #{recommendations}, assessor = #{assessor}, " +
            "assessment_device = #{assessmentDevice}, assessment_time = #{assessmentTime} " +
            "WHERE id = #{id}")
    void update(SmartAssessment assessment);

    @Delete("DELETE FROM smart_assessment WHERE id = #{id}")
    void deleteById(@Param("id") Long id);

    @Select("SELECT * FROM smart_assessment WHERE elder_id = #{elderId} AND assessment_type = #{type} " +
            "ORDER BY assessment_time DESC LIMIT 1")
    SmartAssessment findLatestByElderIdAndType(@Param("elderId") Long elderId, @Param("type") String type);
}
