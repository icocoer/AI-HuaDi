package com.example.work_program.modules.datacollection.mapper;

import com.example.work_program.modules.datacollection.entity.HealthDataCollection;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface HealthDataCollectionMapper {
    @Select("<script>SELECT c.*, e.name as elder_name FROM health_data_collection c " +
            "LEFT JOIN elder_health_record e ON c.elder_id = e.id " +
            "<where>" +
            "<if test='elderId != null'> AND c.elder_id = #{elderId}</if>" +
            "<if test='dataSource != null and dataSource != \"\"'> AND c.data_source = #{dataSource}</if>" +
            "</where> ORDER BY c.collection_date DESC LIMIT #{offset}, #{limit}</script>")
    List<HealthDataCollection> findAll(@Param("elderId") Long elderId, @Param("dataSource") String dataSource, @Param("offset") int offset, @Param("limit") int limit);

    @Select("<script>SELECT COUNT(*) FROM health_data_collection <where>" +
            "<if test='elderId != null'> AND elder_id = #{elderId}</if>" +
            "<if test='dataSource != null and dataSource != \"\"'> AND data_source = #{dataSource}</if>" +
            "</where></script>")
    Long count(@Param("elderId") Long elderId, @Param("dataSource") String dataSource);

    @Select("SELECT c.*, e.name as elder_name FROM health_data_collection c " +
            "LEFT JOIN elder_health_record e ON c.elder_id = e.id WHERE c.id = #{id}")
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

    @Select("<script>SELECT data_source, COUNT(*) as count FROM health_data_collection " +
            "<where><if test='elderId != null'> AND elder_id = #{elderId}</if></where>" +
            " GROUP BY data_source</script>")
    List<Map<String, Object>> countByDataSource(@Param("elderId") Long elderId);

    @Select("SELECT * FROM health_data_collection WHERE elder_id = #{elderId} " +
            "AND collection_date >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "ORDER BY collection_date ASC")
    List<HealthDataCollection> findRecentByElderId(@Param("elderId") Long elderId, @Param("days") int days);
}
