package com.learnforge.course.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Third-Level Category
 * @ClassName CateSimpleInfoVO
 * @Author wusongsong
 * @Date 2022/7/11 20:59
 * @Version
 **/
@Data
@ApiModel("Category")
public class CateSimpleInfoDTO {
    @ApiModelProperty("Primary Category")
    private Long firstCateId;
    @ApiModelProperty("Second-level Category ID")
    private Long secondCateId;
    @ApiModelProperty("Third-level Category ID")
    private Long thirdCateId;

}
