package com.example.work_program.modules.system.mapper;

import com.example.work_program.modules.system.entity.SysDict;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface SysDictMapper {

    @Select("SELECT * FROM sys_dict WHERE dict_type = #{dictType} ORDER BY sort")
    List<SysDict> findByType(String dictType);

    @Select("SELECT * FROM sys_dict WHERE id = #{id}")
    SysDict findById(Long id);

    @Select("SELECT DISTINCT dict_type FROM sys_dict")
    List<String> findAllTypes();

    @Insert("INSERT INTO sys_dict (id, dict_type, dict_key, dict_value, sort, remark) " +
            "VALUES (#{id}, #{dictType}, #{dictKey}, #{dictValue}, #{sort}, #{remark})")
    int insert(SysDict dict);

    @Update("UPDATE sys_dict SET dict_type=#{dictType}, dict_key=#{dictKey}, dict_value=#{dictValue}, " +
            "sort=#{sort}, remark=#{remark} WHERE id=#{id}")
    int update(SysDict dict);

    @Delete("DELETE FROM sys_dict WHERE id=#{id}")
    int deleteById(Long id);
}
