package com.example.work_program.modules.homecare.mapper;

import com.example.work_program.modules.homecare.entity.Message;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface MessageMapper {
    @Select("<script>SELECT * FROM homecare_message <where>" +
            "<if test='senderId != null'> AND sender_id = #{senderId}</if>" +
            "<if test='receiverId != null'> AND receiver_id = #{receiverId}</if>" +
            "<if test='isRead != null'> AND is_read = #{isRead}</if>" +
            "</where> ORDER BY create_time DESC LIMIT #{offset}, #{limit}</script>")
    List<Message> findAll(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId,
                         @Param("isRead") Integer isRead, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM homecare_message <where>" +
            "<if test='senderId != null'> AND sender_id = #{senderId}</if>" +
            "<if test='receiverId != null'> AND receiver_id = #{receiverId}</if>" +
            "<if test='isRead != null'> AND is_read = #{isRead}</if>" +
            "</where></script>")
    Long count(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId, @Param("isRead") Integer isRead);

    @Select("SELECT COUNT(*) FROM homecare_message WHERE receiver_id = #{receiverId} AND is_read = 0")
    Long countUnread(@Param("receiverId") Long receiverId);

    @Update("UPDATE homecare_message SET is_read = 1 WHERE id = #{id}")
    void markAsRead(@Param("id") Long id);

    @Insert("INSERT INTO homecare_message (id, sender_id, receiver_id, message_type, content, is_read, create_time) " +
            "VALUES (#{id}, #{senderId}, #{receiverId}, #{messageType}, #{content}, #{isRead}, NOW())")
    void insert(Message message);
}