package com.learnforge.search.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Course Information")
public class CourseVO {
    @ApiModelProperty(value = "Course ID", example = "1")
    private Long id;
    @ApiModelProperty(value = "Course Name", example = "Java")
    private String name;
    @ApiModelProperty(value = "CoursePrice, Unit in Fen", example = "32900")
    private Long price;
    @ApiModelProperty(value = "Teacher Name", example = "Mr. Zhang")
    private String teacher;
    @ApiModelProperty(value = "Teacher Avatar", example = "default-user-icon.jpg")
    private String icon;
    @ApiModelProperty(value = "Course duration, unit seconds", example = "3280")
    private Integer duration;
    @ApiModelProperty(value = "CourseCoverUrl", example = "default-cover-url.jpg")
    private String coverUrl;
    @ApiModelProperty(value = "CourseChapterCount", example = "25")
    private Integer sections;
    @ApiModelProperty(value = "Course Enrollment Count (Sales)", example = "3920")
    private Integer sold;

    public static final String[] EXCLUDE_FIELDS =
            {"categoryIdLv1", "categoryIdLv2", "categoryIdLv3", "free",
                    "publishTime", "type", "status", "score"};
}
