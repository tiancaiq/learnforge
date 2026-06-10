package com.learnforge.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learnforge.course.domain.po.CourseContentDraft;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Course Content, Mainly Large Text Mapper Interface
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-18
 */
public interface CourseContentDraftMapper extends BaseMapper<CourseContentDraft> {

    String COLUMNS ="id,course_introduce,use_people,course_detail,dep_id,create_time,update_time,creater,updater,deleted";

    @Insert("insert into course_content_draft (" + COLUMNS +
            ") (select " + COLUMNS + " from course_content where id=#{id})")
    int insertFromCourseContent(@Param("id") Long id);

}
