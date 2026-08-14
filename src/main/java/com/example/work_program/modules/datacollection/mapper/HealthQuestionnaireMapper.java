package com.example.work_program.modules.datacollection.mapper;

import com.example.work_program.modules.datacollection.entity.HealthQuestionnaire;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HealthQuestionnaireMapper {
    @Select("SELECT * FROM health_questionnaire WHERE id = #{id}")
    HealthQuestionnaire findById(@Param("id") Long id);

    @Select("SELECT * FROM health_questionnaire WHERE elder_id = #{elderId} ORDER BY survey_time DESC")
    List<HealthQuestionnaire> findByElderId(@Param("elderId") Long elderId);

    @Select("SELECT * FROM health_questionnaire WHERE collection_id = #{collectionId}")
    List<HealthQuestionnaire> findByCollectionId(@Param("collectionId") Long collectionId);

    @Select("<script>SELECT * FROM health_questionnaire <where>" +
            "<if test='elderId != null'> AND elder_id = #{elderId}</if>" +
            "<if test='questionnaireType != null and questionnaireType != \"\"'> AND questionnaire_type = #{questionnaireType}</if>" +
            "</where> ORDER BY survey_time DESC LIMIT #{offset}, #{limit}</script>")
    List<HealthQuestionnaire> findAll(@Param("elderId") Long elderId, @Param("questionnaireType") String questionnaireType, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM health_questionnaire <where>" +
            "<if test='elderId != null'> AND elder_id = #{elderId}</if>" +
            "<if test='questionnaireType != null and questionnaireType != \"\"'> AND questionnaire_type = #{questionnaireType}</if>" +
            "</where></script>")
    Long count(@Param("elderId") Long elderId, @Param("questionnaireType") String questionnaireType);

    @Insert("INSERT INTO health_questionnaire (id, collection_id, elder_id, questionnaire_type, questions, " +
            "answers, risk_factors, summary, surveyor, survey_time, create_time) " +
            "VALUES (#{id}, #{collectionId}, #{elderId}, #{questionnaireType}, #{questions}, " +
            "#{answers}, #{riskFactors}, #{summary}, #{surveyor}, #{surveyTime}, NOW())")
    void insert(HealthQuestionnaire questionnaire);

    @Update("UPDATE health_questionnaire SET questionnaire_type = #{questionnaireType}, " +
            "questions = #{questions}, answers = #{answers}, risk_factors = #{riskFactors}, " +
            "summary = #{summary}, surveyor = #{surveyor}, survey_time = #{surveyTime} " +
            "WHERE id = #{id}")
    void update(HealthQuestionnaire questionnaire);

    @Delete("DELETE FROM health_questionnaire WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
