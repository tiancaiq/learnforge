package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "CourseAndDirectoryAndLearningProgressInfo")
public class CourseAndSectionVO {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("Course Name")
    private String name;
    @ApiModelProperty("Course Cover")
    private String coverUrl;
    @ApiModelProperty("CourseChapterCount")
    private Integer sections;
    @ApiModelProperty("TeacherAvatar")
    private String teacherIcon;
    @ApiModelProperty("TeacherName")
    private String teacherName;
    @ApiModelProperty("id")
    private Long lessonId;
    @ApiModelProperty("CurrentLearningSectionId")
    private Long latestSectionId;
    private List<ChapterVO> chapters;
}