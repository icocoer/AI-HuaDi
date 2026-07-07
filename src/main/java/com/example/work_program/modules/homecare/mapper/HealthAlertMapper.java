package com.example.work_program.modules.homecare.mapper;

import com.example.work_program.modules.homecare.entity.HealthAlert;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface HealthAlertMapper {
    @Select("<script>SELECT * FROM homecare_health_alert <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='isRead != null'>AND is_read = #{isRead}</if>" +
            "</where> ORDER BY create_time DESC LIMIT #{offset}, #{limit}</script>")
    List<HealthAlert> findAll(@Param("elderId") Long elderId, @Param("isRead") Integer isRead,
                             @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM homecare_health_alert <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='isRead != null'>AND is_read = #{isRead}</if>" +
            "</where></script>")
    Long count(@Param("elderId") Long elderId, @Param("isRead") Integer isRead);

    @Select("SELECT COUNT(*) FROM homecare_health_alert WHERE is_read = 0")
    Long countUnread();

    @Update("UPDATE homecare_health_alert SET is_read = 1 WHERE id = #{id}")
    void markAsRead(@Param("id") Long id);

    @Insert("INSERT INTO homecare_health_alert (id, elder_id, alert_type, alert_level, alert_message, is_read, create_time) " +
            "VALUES (#{id}, #{elderId}, #{alertType}, #{alertLevel}, #{alertMessage}, #{isRead}, NOW())")
    void insert(HealthAlert alert);
}