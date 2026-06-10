package com.learnforge.course.domain.dto;

import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.DateUtils;
import com.learnforge.common.validate.Checker;
import com.learnforge.course.constants.CourseErrorInfo;
import com.learnforge.course.utils.CourseSaveBaseGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Course Basic Info
 *
 * @ClassName CourseBaseInfoSaveDTO
 * @author wusongsong
 * @since 2022/7/11 11:39
 * @version 1.0.0
 **/

@Data
@ApiModel(description = "Save Course Basic Info")
public class CourseBaseInfoSaveDTO implements Checker {
    @ApiModelProperty("Course ID, For New Courses This Value Cannot Be Passed, For Old Courses It Must Be Filled")
    private Long id;
    @ApiModelProperty("Course Name")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_SAVE_NAME_NULL)
    private String name;
    @ApiModelProperty("Third-level Course Category ID")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_SAVE_CATEGORY_NULL)
    private Long thirdCateId;
    @ApiModelProperty("Cover Link URL")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_SAVE_COVER_URL_NULL, groups = CourseSaveBaseGroup.class)
    private String coverUrl;
    @ApiModelProperty("Is Free")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_SAVE_FREE_NULL)
    private Boolean free;
    @ApiModelProperty("Course Price")
    @Min(value = 0, message = CourseErrorInfo.Msg.COURSE_SAVE_PRICE_NEGATIVE)
    private Integer price;
//    @ApiModelProperty("Purchase Start Time")
//    @NotNull(message = CourseErrorInfo.Msg.COURSE_SAVE_PURCHASE_TIME_NULL)
    private LocalDateTime purchaseStartTime;
    @ApiModelProperty("Purchase End Time")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_SAVE_PURCHASE_TIME_NULL)
    private LocalDateTime purchaseEndTime;
    @ApiModelProperty("Course Description")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_SAVE_INTRODUCE_NULL, groups = CourseSaveBaseGroup.class)
    private String introduce;
    @ApiModelProperty("Target Audience")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_SAVE_USE_PEOPLE_NULL, groups = CourseSaveBaseGroup.class)
    private String usePeople;
    @ApiModelProperty("Details")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_SAVE_DETAIL_NULL, groups = CourseSaveBaseGroup.class)
    private String detail;
    @ApiModelProperty("Learning Duration, 0 or Not Passed Indicates No Limit, Other Indicates Months")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_SAVE_DURATION_NULL)
    private Integer validDuration;

    @Override
    public void check() {
        if(!free) { //Non-Free
            if(price == null) { //Paid Course Without Set Price
                throw new BadRequestException(CourseErrorInfo.Msg.COURSE_SAVE_PRICE_NULL);
            }
            if(price <= 0){ //Paid Course Set Price Less Than 0
                throw new BadRequestException(CourseErrorInfo.Msg.COURSE_SAVE_PRICE_NEGATIVE);
            }
        }else { //Free
            if(price != null && price > 0){ //Free Course Set Price
                throw new BadRequestException(CourseErrorInfo.Msg.COURSE_SAVE_PRICE_FREE);
            }
        }
        if(purchaseEndTime.isBefore(DateUtils.now())){
            throw new BadRequestException(CourseErrorInfo.Msg.COURSE_SAVE_PURCHASE_ILLEGAL);
        }
//        if (purchaseStartTime.isAfter(purchaseEndTime)) {
//            throw new BadRequestException(CourseErrorInfo.Msg.COURSE_SAVE_PURCHASE_ILLEGAL);
//        }
//        if(id == null && purchaseStartTime.isBefore(LocalDateTime.now())){
//            throw new BadRequestException(CourseErrorInfo.Msg.COURSE_SAVE_PURCHASE_ILLEGAL2);
//        }
    }
}
