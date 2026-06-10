package com.learnforge.trade.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Shopping cart item information")
public class CartVO {
    @ApiModelProperty("Shopping cart item id")
    private Long id;
    @ApiModelProperty("Course ID")
    private Long courseId;
    @ApiModelProperty("Course Name")
    private String courseName;
    @ApiModelProperty("Course cover url")
    private String coverUrl;
    @ApiModelProperty("Course price at the time of adding to cart, unit: yuan")
    private Integer price;
    @ApiModelProperty("Current course price, unit: yuan")
    private Integer nowPrice;
    @ApiModelProperty("Whether the course has expired")
    private Boolean expired;
    @JsonIgnore
    @ApiModelProperty(value = "CourseValidity", hidden = true)
    private LocalDateTime courseValidDate;
}