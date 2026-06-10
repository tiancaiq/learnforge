package com.learnforge.data.model.vo;

import com.learnforge.common.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName AxisVO
 * @Author wusongsong
 * @Date 2022/10/10 10:55
 * @Version
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AxisVO {

    //Value Axis
    public static final String TYPE_VALUE = "value";
    //Category Axis
    public static final String TYPE_CATEGORY = "category";
    //Time Axis
    public static final String TYPE_TIME = "time";
    //Logarithmic Axis
    public static final String TYPE_LOG = "log";

    private String type;
    //Maximum Value with Unit
    private Double max;
    //Minimum Value with Unit
    private Double min;
    //Average Value without Unit
    private Double average;
    // Data
    private List<?> data;
    //interval
    private Double interval;


    public static AxisVO last15Day() {

        return new AxisVO(
                TYPE_CATEGORY,
                null,
                null,
                null,
                DateUtils.last15Day(),
                null
        );
    }

}
