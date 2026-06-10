package com.learnforge.course.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wusongsong
 * @since 2022/7/26 9:26
 * @version 1.0.0
 **/
@Data
public class CourseSimpleInfoListDTO {

    @ApiModelProperty("Tertiary Category ID List")
    private List<Long> thirdCataIds;

    @ApiModelProperty("Course ID List")
    private List<Long> ids;
}
