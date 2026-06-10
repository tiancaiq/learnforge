package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * CourseRelatedTeacherInfo
 * @ClassName CourseTeacherVO
 * @Author wusongsong
 * @Date 2022/7/11 18:17
 * @Version
 **/
@Data
@ApiModel("TeacherCourseInfo")
public class CourseTeacherVO {
    @ApiModelProperty("Teacher Course Relation ID")
    private Long id;
    @ApiModelProperty("Teacher Avatar")
    private String icon;
    @ApiModelProperty("Profile Photo")
    private String photo;
    @ApiModelProperty("Teacher Name")
    private String name;
    @ApiModelProperty("Teacher Introduction")
    private String introduce;
    @ApiModelProperty("Display on User End")
    private Boolean isShow;
    @ApiModelProperty("Position")
    private String job;

}
