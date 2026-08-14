package com.example.work_program.modules.elder.mapper;

import com.example.work_program.modules.elder.entity.ElderHealthRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ElderHealthRecordMapper {
    @Select("<script>SELECT * FROM elder_health_record <where>" +
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='riskLevel != null and riskLevel != \"\"'> AND risk_level = #{riskLevel}</if>" +
            "</where> ORDER BY create_time DESC LIMIT #{offset}, #{limit}</script>")
    List<ElderHealthRecord> findAll(@Param("name") String name, @Param("riskLevel") String riskLevel, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM elder_health_record <where>" +
            "<if test='name != null and name != \"\"'> AND name LIKE CONCAT('%', #{name}, '%')</if>" +
            "<if test='riskLevel != null and riskLevel != \"\"'> AND risk_level = #{riskLevel}</if>" +
            "</where></script>")
    Long count(@Param("name") String name, @Param("riskLevel") String riskLevel);

    @Select("SELECT * FROM elder_health_record WHERE id = #{id}")
    ElderHealthRecord findById(@Param("id") Long id);

    @Insert("INSERT INTO elder_health_record (id, name, gender, birth_date, id_card, phone, address, " +
            "emergency_contact, emergency_phone, medical_history, family_history, " +
            "cognitive_baseline, risk_level, status, create_by, update_by, create_time, update_time) " +
            "VALUES (#{id}, #{name}, #{gender}, #{birthDate}, #{idCard}, #{phone}, #{address}, " +
            "#{emergencyContact}, #{emergencyPhone}, #{medicalHistory}, #{familyHistory}, " +
            "#{cognitiveBaseline}, #{riskLevel}, #{status}, #{createBy}, #{updateBy}, NOW(), NOW())")
    void insert(ElderHealthRecord record);

    @Update("UPDATE elder_health_record SET name = #{name}, gender = #{gender}, birth_date = #{birthDate}, " +
            "id_card = #{idCard}, phone = #{phone}, address = #{address}, " +
            "emergency_contact = #{emergencyContact}, emergency_phone = #{emergencyPhone}, " +
            "medical_history = #{medicalHistory}, family_history = #{familyHistory}, " +
            "cognitive_baseline = #{cognitiveBaseline}, risk_level = #{riskLevel}, " +
            "status = #{status}, update_by = #{updateBy}, update_time = NOW() WHERE id = #{id}")
    void update(ElderHealthRecord record);

    @Delete("DELETE FROM elder_health_record WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
