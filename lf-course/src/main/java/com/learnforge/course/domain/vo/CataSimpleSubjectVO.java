package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wusongsong
 * @since 2022/8/15 16:04
 * @version 1.0.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CataSimpleSubjectVO {
    @ApiModelProperty("Section or Practice ID")
    private Long cataId;
    @ApiModelProperty("Question ID")
    private List<SubjectInfo> subjects;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectInfo{
        private Long id;
        private String name;
    }
}
