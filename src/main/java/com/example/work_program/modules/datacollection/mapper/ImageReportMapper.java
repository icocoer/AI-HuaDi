package com.example.work_program.modules.datacollection.mapper;

import com.example.work_program.modules.datacollection.entity.ImageReport;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageReportMapper {
    @Select("SELECT * FROM image_report WHERE id = #{id}")
    ImageReport findById(@Param("id") Long id);

    @Select("SELECT * FROM image_report WHERE elder_id = #{elderId} ORDER BY upload_time DESC")
    List<ImageReport> findByElderId(@Param("elderId") Long elderId);

    @Select("SELECT * FROM image_report WHERE collection_id = #{collectionId}")
    List<ImageReport> findByCollectionId(@Param("collectionId") Long collectionId);

    @Select("<script>SELECT * FROM image_report <where>" +
            "<if test='elderId != null'>AND elder_id = #{elderId}</if>" +
            "<if test='imageType != null and imageType != \"\"'>AND image_type = #{imageType}</if>" +
            "</where> ORDER BY upload_time DESC</script>")
    List<ImageReport> findAll(@Param("elderId") Long elderId, @Param("imageType") String imageType);

    @Insert("INSERT INTO image_report (id, collection_id, elder_id, image_type, image_url, thumb_url, " +
            "report_no, institution, doctor_name, diagnosis_date, diagnosis_result, " +
            "diagnosis_description, abnormal_indicators, upload_time) " +
            "VALUES (#{id}, #{collectionId}, #{elderId}, #{imageType}, #{imageUrl}, #{thumbUrl}, " +
            "#{reportNo}, #{institution}, #{doctorName}, #{diagnosisDate}, #{diagnosisResult}, " +
            "#{diagnosisDescription}, #{abnormalIndicators}, NOW())")
    void insert(ImageReport report);

    @Update("UPDATE image_report SET image_type = #{imageType}, image_url = #{imageUrl}, " +
            "thumb_url = #{thumbUrl}, report_no = #{reportNo}, institution = #{institution}, " +
            "doctor_name = #{doctorName}, diagnosis_date = #{diagnosisDate}, " +
            "diagnosis_result = #{diagnosisResult}, diagnosis_description = #{diagnosisDescription}, " +
            "abnormal_indicators = #{abnormalIndicators} WHERE id = #{id}")
    void update(ImageReport report);

    @Delete("DELETE FROM image_report WHERE id = #{id}")
    void deleteById(@Param("id") Long id);

    @Select("SELECT COUNT(*) FROM image_report WHERE elder_id = #{elderId}")
    Long countByElderId(@Param("elderId") Long elderId);
}
