package com.learnforge.promotion.domain.dto;

import com.learnforge.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "Coupon Issue Form DTO")
public class CouponIssueFormDTO {
    @ApiModelProperty("Id")
    private Long id;
    @ApiModelProperty("Issue Begin Time")
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    @Future(message = "Issue Begin Time must be in the future")
    private LocalDateTime issueBeginTime;
    @ApiModelProperty("Issue End Time")
    @Future(message = "Issue End Time must be in the future")
    @NotNull(message = "Issue End Time is required")
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime issueEndTime;


    @ApiModelProperty("Term Days")
    private Integer termDays;
    @ApiModelProperty("Term Begin Time")
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime termBeginTime;
    @ApiModelProperty("Term End Time")
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime termEndTime;
}
