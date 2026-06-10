package com.learnforge.course.domain.dto;

import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.common.validate.Checker;
import com.learnforge.course.constants.CourseConstants;
import com.learnforge.course.constants.CourseErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author wusongsong
 * @since 2022/7/11 16:49
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Chapter")
public class CataSaveDTO implements Checker {
    @ApiModelProperty("Chapter, section, exercise id")
    private Long id;
    @ApiModelProperty("Directory type 1: chapter, 2: section, 3: test")
    @NotNull(message = "")
    private Integer type;
    @ApiModelProperty("Chapter and exercise name")
    private String name;
    @ApiModelProperty("Chapter Sort, Chapter Must Be Passed, Section and Practice Do Not Need to Be Passed")
    private Integer index;

    @ApiModelProperty("Current Chapter's Sections or Practices")
    @Size(min = 1, message = "Cannot Have Empty Chapters")
    private List<CataSaveDTO> sections;

    @Override
    public void check() {
        //Name Empty Check
        if(type == CourseConstants.CataType.CHAPTER && StringUtils.isEmpty(name)) {
            throw new BadRequestException(CourseErrorInfo.Msg.COURSE_CATAS_SAVE_NAME_NULL);
        }else if(StringUtils.isEmpty(name)){
            throw new BadRequestException(CourseErrorInfo.Msg.COURSE_CATAS_SAVE_NAME_NULL2);
        }
        //Name Length Issue
        if (type == CourseConstants.CataType.CHAPTER && name.length() > 30){
            throw new BadRequestException(CourseErrorInfo.Msg.COURSE_CATAS_SAVE_NAME_SIZE);
        }else if(name.length() > 30) {
            throw new BadRequestException(CourseErrorInfo.Msg.COURSE_CATAS_SAVE_NAME_SIZE2);
        }
        if(CollUtils.isEmpty(sections)){
            throw new BadRequestException("Cannot Have Empty Chapters");
        }

    }
}
