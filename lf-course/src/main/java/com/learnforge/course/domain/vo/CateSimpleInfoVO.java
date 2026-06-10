package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Third-Level Category
 * @author wusongsong
 * @since 2022/7/11 20:59
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Category")
public class CateSimpleInfoVO {
    @ApiModelProperty("Primary Category")
    private Long firstCateId;
    @ApiModelProperty("PrimaryClassificationName")
    private String firstCateName;
    @ApiModelProperty("Second-level Category ID")
    private Long secondCateId;
    @ApiModelProperty("SecondaryClassificationName")
    private String secondCateName;
    @ApiModelProperty("Third-level Category ID")
    private Long thirdCateId;
    @ApiModelProperty("TertiaryClassificationName")
    private String thirdCateName;

}
