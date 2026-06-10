package com.learnforge.api.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Course Information
 *
 * @author wusongsong
 * @since 2022/8/5 16:54
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Course Details, Including Course, Chapters, Teacher")
public class CourseFullInfoDTO {
    @ApiModelProperty("Course ID")
    private Long id;
    @ApiModelProperty("Course Name")
    private String name;
    @ApiModelProperty("Cover Link")
    private String coverUrl;
    @ApiModelProperty("Price")
    private Integer price;
    @ApiModelProperty("First-level Course Category ID")
    private Long firstCateId;
    @ApiModelProperty("Second-level Course Category ID")
    private Long secondCateId;
    @ApiModelProperty("Third-level Course Category ID")
    private Long thirdCateId;
    @ApiModelProperty("Total Course Chapters")
    private Integer sectionNum;
    @ApiModelProperty("Course Purchase Validity End Time")
    private LocalDateTime purchaseEndTime;
    @ApiModelProperty("Course Learning Validity")
    private Integer validDuration;
    @ApiModelProperty("Course Chapter Info")
    private List<CatalogueDTO> chapters;
    @ApiModelProperty("Teacher List")
    private List<Long> teacherIds;
    @JsonIgnore
    public List<Long> getCategoryIds(){
        return List.of(firstCateId, secondCateId, thirdCateId);
    }
}
