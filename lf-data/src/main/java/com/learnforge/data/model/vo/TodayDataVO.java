package com.learnforge.data.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName TodayDataVO
 * @Author wusongsong
 * @Date 2022/10/13 9:23
 * @Version
 **/
@Data
public class TodayDataVO {
    @ApiModelProperty("Visits, Unit in Ten Thousand Times")
    private Double visits;
    @ApiModelProperty("Today's Order Amount, Unit in Ten Thousand Yuan")
    private Double orderAmount;
    @ApiModelProperty("Today's Order Count")
    private Integer orderNum;
    @ApiModelProperty("Today's New Student Count")
    private Integer stuNewNum;
}
