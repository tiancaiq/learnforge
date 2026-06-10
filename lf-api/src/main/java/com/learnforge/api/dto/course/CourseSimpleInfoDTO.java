package com.learnforge.api.dto.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wusongsong
 * @since 2022/7/27 14:32
 * @version 1.0.0
 **/
@Data
public class CourseSimpleInfoDTO {
    @ApiModelProperty("Course ID")
    private Long id;
    @ApiModelProperty("Course Name")
    private String name;
    @ApiModelProperty("Cover URL")
    private String coverUrl;
    @ApiModelProperty("Price")
    private Integer price;
    @ApiModelProperty("Course Status")
    private Integer status;
    @ApiModelProperty("Is Free Course")
    private Boolean free;
    @ApiModelProperty("First-level Category ID")
    private Long firstCateId;
    @ApiModelProperty("Second-level Category ID")
    private Long secondCateId;
    @ApiModelProperty("Third-level Category ID")
    private Long thirdCateId;
    @ApiModelProperty("Section Count")
    private Integer sectionNum;
    @ApiModelProperty("Course Purchase Validity End Time")
    private LocalDateTime purchaseEndTime;
    @ApiModelProperty("Course Learning Validity, Unit: Month")
    private Integer validDuration;
    @JsonIgnore
    public List<Long> getCategoryIds(){
        return List.of(firstCateId, secondCateId, thirdCateId);
    }
}
