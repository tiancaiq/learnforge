package com.learnforge.course.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName CourseStatus
 * @Author wusongsong
 * @Date 2022/7/18 16:07
 * @Version
 **/
@Getter
@AllArgsConstructor
public enum CourseStatus {
    NO_UP_SHELF(1, "Pending publication"),
    SHELF(2, "Published"),
    DOWN_SHELF(3, "Offline"),
    FINISHED(4, "Completed");
    private Integer status;
    private String desc;

    public static String desc(Integer status) {
        for (CourseStatus courseStatus : values()) {
            if (courseStatus.getStatus() == status) {
                return courseStatus.getDesc();
            }
        }
        return null;
    }

    public boolean equals(Integer status){
        return this.status.intValue() == status;
    }
}