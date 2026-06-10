package com.learnforge.course.domain.dto;

import com.learnforge.course.constants.CourseErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Course Video Model
 *
 * @author wusongsong
 * @since 2022/7/11 17:12
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Course Video Model")
public class CourseMediaDTO {
    @ApiModelProperty("Directory id")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_MEDIA_NULL)
    private Long cataId;
    @ApiModelProperty("Media asset id")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_MEDIA_NULL)
    private Long mediaId;
    @ApiModelProperty("Support Trial Viewing")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_MEDIA_NULL)
    private Boolean trailer;
    @ApiModelProperty("Media asset name")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_MEDIA_NULL)
    @Length(min = 1, message = CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_MEDIA_NULL)
    private String videoName;
    @ApiModelProperty("Media Duration, Unit s")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_MEDIA_NULL)
    @Min(value = 1, message = CourseErrorInfo.Msg.COURSE_MEDIA_SAVE_MEDIA_NULL)
    private Integer mediaDuration;
}
