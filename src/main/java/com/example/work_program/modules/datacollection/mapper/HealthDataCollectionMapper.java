package com.example.work_program.modules.datacollection.mapper;

import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HealthDataCollectionMapper {
    @Select("<script>SELECT * FROM health_data_collection <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='dataSource != null and dataSource != \"\"'>AND data_source = #{dataSource}</if>" +
            "</where> ORDER BY collection_date DESC</script>")
    List<HealthDataCollection> findAll(@Param("elderId") Long elderId, @Param("dataSource") String dataSource);

    @Select("SELECT * FROM health_data_collection WHERE id = #{id}")
    HealthDataCollection findById(@Param("id") Long id);

    @Insert("INSERT INTO health_data_collection (id, elder_id, data_source, data_type, data_content, " +
            "attachment_url, collection_date, collector, remark, create_time, update_time) " +
            "VALUES (#{id}, #{elderId}, #{dataSource}, #{dataType}, #{dataContent}, " +
            "#{attachmentUrl}, #{collectionDate}, #{collector}, #{remark}, NOW(), NOW())")
    void insert(HealthDataCollection data);

    @Update("UPDATE health_data_collection SET elder_id = #{elderId}, data_source = #{dataSource}, " +
            "data_type = #{dataType}, data_content = #{dataContent}, attachment_url = #{attachmentUrl}, " +
            "collection_date = #{collectionDate}, collector = #{collector}, remark = #{remark}, " +
            "update_time = NOW() WHERE id = #{id}")
    void update(HealthDataCollection data);

    @Delete("DELETE FROM health_data_collection WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
}
