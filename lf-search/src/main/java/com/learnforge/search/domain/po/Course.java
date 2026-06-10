package com.learnforge.search.domain.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Course {
    @Id
    private Long id;
    /** Course Name */
    private String name;
    /** Classification id */
    private Long categoryIdLv1;
    /** Category ID 2 */
    private Long categoryIdLv2;
    /** Category ID 3 */
    private Long categoryIdLv3;
    /** Is Free */
    private Boolean free;
    /** Course Type: 1: Live Class, 2: Recorded Class */
    private Integer type;
    /** Course Sales, Registration Count */
    private Integer sold;
    /** Price */
    private Integer price;
    /** CourseRating */
    private Integer score;
    /** Teacher ID */
    private Long teacher;
    /** ChapterCount */
    private Integer sections;
    /** Course Cover */
    private String coverUrl;
    private LocalDateTime publishTime;

    @JsonIgnore
    public List<Long> getCategoryIds(){
        return List.of(categoryIdLv1, categoryIdLv2, categoryIdLv3);
    }
}
