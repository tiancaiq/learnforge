package com.learnforge.course.domain.dto;

import com.learnforge.course.constants.CourseErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Save Teacher-Course Relationship
 * @ClassName CourseTeacherSaveDTO
 * @Author wusongsong
 * @Date 2022/7/13 14:59
 * @Version
 **/
@Data
@ApiModel("Course Teacher Relationship Model")
public class CourseTeacherSaveDTO {
    @ApiModelProperty("Course ID")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_TEACHER_SAVE_COURSE_ID_NULL)
    private Long id;
    @ApiModelProperty("Teacher ID and Whether to Display on User End, This List Follows the Order on the Interface")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_TEACHER_SAVE_TEACHERS_NULL)
//    @Min(value = 1, message = CourseErrorInfo.Msg.COURSE_TEACHER_SAVE_TEACHERS_NULL)
    @Size(min = 1, max = 5, message = CourseErrorInfo.Msg.COURSE_TEACHER_SAVE_TEACHERS_NUM_MAX )
    private List<TeacherInfo> teachers;

    @Data
    @ApiModel("Teacher ID and Whether to Display on User End")
    public static class TeacherInfo{
        @ApiModelProperty("Teacher ID")
        @NotNull(message = CourseErrorInfo.Msg.COURSE_TEACHER_SAVE_TEACHER_ID_NULL)
        private Long id;
        @ApiModelProperty("Whether to Display on User End")
        @NotNull(message = CourseErrorInfo.Msg.COURSE_TEACHER_SAVE_TEACHER_SHOW)
        private Boolean isShow;
    }
}
