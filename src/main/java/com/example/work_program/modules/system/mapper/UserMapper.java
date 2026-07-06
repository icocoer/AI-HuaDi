package com.example.work_program.modules.system.mapper;

import com.example.work_program.modules.system.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND password = #{password}")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Select("<script>SELECT * FROM sys_user <where>" +
            "<if test='username != null and username != \"\"'>AND username LIKE CONCAT('%', #{username}, '%')</if>" +
            "<if test='realName != null and realName != \"\"'>AND real_name LIKE CONCAT('%', #{realName}, '%')</if>" +
            "</where> ORDER BY create_time DESC</script>")
    List<User> findAll(@Param("username") String username, @Param("realName") String realName);

    @Insert("INSERT INTO sys_user (id, username, password, real_name, phone, email, status, role, create_time, update_time) " +
            "VALUES (#{id}, #{username}, #{password}, #{realName}, #{phone}, #{email}, #{status}, #{role}, NOW(), NOW())")
    void insert(User user);

    @Update("UPDATE sys_user SET real_name = #{realName}, phone = #{phone}, email = #{email}, " +
            "status = #{status}, role = #{role}, update_time = NOW() WHERE id = #{id}")
    void update(User user);

    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
