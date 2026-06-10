package com.learnforge.trade.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProgressNodeVO {
    @ApiModelProperty("Order progress node name")
    private String name;
    @ApiModelProperty("Time corresponding to the order progress node name")
    private LocalDateTime time;
}