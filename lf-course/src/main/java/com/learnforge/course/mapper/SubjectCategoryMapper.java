package com.learnforge.course.mapper;

import com.learnforge.course.domain.po.SubjectCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Course Category Relation Table Mapper Interface
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-15
 */
public interface SubjectCategoryMapper extends BaseMapper<SubjectCategory> {

    @Insert("<script>INSERT INTO subject_category (subject_id,first_cate_id,second_cate_id,third_cate_id) " +
            "value <foreach collection='scs' item='sc'  separator=','>(#{sc.subjectId},#{sc.firstCateId}," +
            "#{sc.secondCateId},#{sc.thirdCateId})</foreach></script>")
    int batchInsert(@Param("scs") List<SubjectCategory> subjectCategoryList);

    /**
     * Get Number of Courses Under Specified Course Category
     *
     * @param categoryId
     * @param level
     * @return
     */
    @Select("<script>select count(*) from subject_category where 1= 1 <if test='level==1'> and first_cate_id=#{categoryId} </if> " +
            "<if test='level==2'> and second_cate_id=#{categoryId} </if>" +
            "<if test='level==3'> and third_cate_id=#{categoryId} </if>" +
            "</script>")
    int countSubjectNum(@Param("categoryId") Long categoryId, @Param("level") Integer level);

}
