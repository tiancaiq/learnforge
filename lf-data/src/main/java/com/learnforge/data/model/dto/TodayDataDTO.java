package com.learnforge.data.model.dto;

import lombok.Data;

/**
 * @ClassName TodayDataVO
 * @Author wusongsong
 * @Date 2022/10/13 9:23
 * @Version
 **/
@Data
public class TodayDataDTO {
    private Integer version;
    //Visits, Unit in Ten Thousand Times
    private Double visits;
    //Today's Order Amount, Unit in Ten Thousand Yuan
    private Double orderAmount;
    //Today's Order Count
    private Integer orderNum;
    //Today's New Student Count
    private Integer stuNewNum;
}
