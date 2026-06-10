package com.learnforge.remark.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Like Record Form Entity")
public class LikeRecordFormDTO {
    @ApiModelProperty("Like Business ID")
    @NotNull(message = "Business ID Cannot Be Empty")
    private Long bizId;

    @ApiModelProperty("Like Business Type")
    @NotNull(message = "Business Type Cannot Be Empty")
    private String bizType;

    @ApiModelProperty("Is Liked, true: Liked; false: Cancel Like")
    @NotNull(message = "Is Liked Cannot Be Empty")
    private Boolean liked;
}
