package com.learnforge.course.domain.dto;

import com.learnforge.course.constants.CourseErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Save Chapter Info
 *
 * @author wusongsong
 * @since 2022/7/11 18:10
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Relationship Model Between Sections, Practices, and Questions")
public class CataSubjectDTO {
        @ApiModelProperty("Question ID")
        @NotNull(message = CourseErrorInfo.Msg.COURSE_SUBJECT_SAVE_SUBJECT_IDS_NULL)
        @Size(min = 1,message = CourseErrorInfo.Msg.COURSE_SUBJECT_SAVE_SUBJECT_IDS_NULL)
        private List<Long> subjectIds;
        @ApiModelProperty("Section or Practice ID")
        @NotNull(message = CourseErrorInfo.Msg.COURSE_SUBJECT_SAVE_CATALOGUE_ID_NULL)
        private Long cataId;
}
