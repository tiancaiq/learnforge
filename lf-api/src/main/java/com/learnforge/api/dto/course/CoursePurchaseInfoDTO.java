package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Course Payment Information, Course Status
 * @author wusongsong
 * @since 2022/7/26 20:41
 * @version 1.0.0
 **/
@Data
@ApiModel("Course Purchase Info")
@NoArgsConstructor
@AllArgsConstructor
public class CoursePurchaseInfoDTO {
    @ApiModelProperty("Enrollment Count")
    private Integer enrollNum;
    @ApiModelProperty("Refund Count")
    private Integer refundNum;
    @ApiModelProperty("Actual Paid Total Amount")
    private Integer realPayAmount;
}
