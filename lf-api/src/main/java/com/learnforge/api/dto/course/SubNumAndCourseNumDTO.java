package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Teacher's Course Count and Question Count Collection
 * @ClassName SubNumAndCourseNumDTO
 * @author wusongsong
 * @since 2022/7/18 15:12
 * @version 1.0.0
 **/
@Data
@AllArgsConstructor
@NotNull
@ApiModel("Teacher ID and Corresponding Course Count, Question Count")
public class SubNumAndCourseNumDTO {
    @ApiModelProperty("Teacher ID")
    private Long teacherId;
    @ApiModelProperty("Teacher's Course Count")
    private Integer courseNum;
    @ApiModelProperty("Teacher's Question Count")
    private Integer subjectNum;
}
