package com.learnforge.course.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Draft Course
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Course Draft ID, Corresponds to Formal Draft ID
     */
    private Long id;

    /**
     * Course Name
     */
    private String name;

    /**
     * Course Type, 1: Live Course, 2: Recorded Course
     */
    private Integer courseType;

    /**
     * Cover Link
     */
    private String coverUrl;

    /**
     * First-level Course Category ID
     */
    private Long firstCateId;

    /**
     * Second-level Course Category ID
     */
    private Long secondCateId;

    /**
     * Third-level Course Category ID
     */
    private Long thirdCateId;

    /**
     * Sales Method 0: Paid, 1: Free
     */
    private Integer free;

    /**
     * Course Price, Unit in Fen
     */
    private Integer price;

    /**
     * Template Type, 1: Fixed Template, 2: Custom Template
     */
    private Integer templateType;

    /**
     * Custom Template Link
     */
    private String templateUrl;

    /**
     * Course Status, 1: Pending Upload, 2: Uploaded, 3: Offline, 4: Completed
     */
    private Integer status;

    /**
     * Course Purchase Validity Start Time
     */
    private LocalDateTime purchaseStartTime;

    /**
     * Course Purchase Validity End Time
     */
    private LocalDateTime purchaseEndTime;

    /**
     * Information Filling Progress
     */
    private Integer step;

    /**
     * Course Rating Score, 45 Represents 4.5 Stars
     */
    private Integer score;

    /**
     * Total Course Duration
     */
    private Integer mediaDuration;

    /**
     * Course Validity, Unit in Months
     */
    private Integer validDuration;

    /**
     * Total Number of Course Sections, Including Practice
     */
    private Integer sectionNum;

    /**
     * Department id
     */
    private Long depId;

    /**
     * Publish Count
     */
    private Integer publishTimes;

    /**
     * Last Publish Time
     */
    private LocalDateTime publishTime;

    /**
     * Creation Time
     */
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;

    /**
     * Creator
     */
    private Long creater;

    /**
     * Updater
     */
    private Long updater;

    /**
     * Logical Deletion
     */
    private Integer deleted;


}
