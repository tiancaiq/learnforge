package com.learnforge.course.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Course Video Save Model
 * @author wusongsong
 * @since 2022/7/13 15:09
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Course Video Save Model")
public class CourseMediaSaveDTO {
    @ApiModelProperty("Section ID")
    private Long cataId;
    @ApiModelProperty("Media asset id")
    private Long mediaId;
    @ApiModelProperty("Support Trial Viewing")
    private Boolean trailer;
}
