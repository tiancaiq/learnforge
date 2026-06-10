package com.learnforge.data.model.vo;

import com.learnforge.data.model.po.CourseInfo;
import lombok.Data;

import java.util.List;

/**
 * @ClassName Top10DataVO
 * @Author wusongsong
 * @Date 2022/10/10 19:33
 * @Version
 **/
@Data
public class Top10DataVO {
    // Popular Courses
    private List<CourseInfo> hot;
    // Best-selling Courses
    private List<CourseInfo> hotSales;
}
