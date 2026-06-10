package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wusongsong
 * @since 2022/7/27 14:22
 * @version 1.0.0
 **/
@Data
public class CataSimpleInfoDTO {
    @ApiModelProperty("Directory id")
    private Long id;
    @ApiModelProperty("Directory name")
    private String name;
    @ApiModelProperty("Digital sequence number, does not include chapter sequence number")
    private Integer cIndex;
}
