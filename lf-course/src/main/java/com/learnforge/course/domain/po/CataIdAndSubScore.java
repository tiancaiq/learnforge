package com.learnforge.course.domain.po;

import lombok.Data;

/**
 * Query Practice ID Corresponding to Practice in a Course and the Question ID Corresponding to the Practice
 * @author wusongsong
 * @since 2022/7/22 16:04
 * @version 1.0.0
 **/
@Data
public class CataIdAndSubScore {
    //Directory id
    private Long cataId;
    //Question ID
    private Long subjectId;
    //Score of the Question
    private Integer score;
}
