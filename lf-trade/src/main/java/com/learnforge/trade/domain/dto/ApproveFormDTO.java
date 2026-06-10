package com.learnforge.trade.domain.dto;

import com.learnforge.common.validate.annotations.EnumValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Refund Approval Model")
public class ApproveFormDTO{
    @ApiModelProperty("Refund ID")
    @NotNull(message = "Refund ID Cannot Be Empty")
    private Long id;
    @NotNull(message = "Approval Type Cannot Be Empty")
    @EnumValid(enumeration = {1,2}, message = "Approval Only Allows Agree and Reject Operations")
    @ApiModelProperty("Approval Type, 1: Agree, 2: Reject")
    public Integer approveType;
    @ApiModelProperty("Approval Comment")
    private String approveOpinion;
    @ApiModelProperty("Remarks")
    private String remark;
}