package com.learnforge.data.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @ClassName DataTypeEnum
 * @Author wusongsong
 * @Date 2022/10/10 15:28
 * @Version
 **/
@AllArgsConstructor
@Getter
public enum DataTypeEnum {

    VISITS(1, "Visits", "Times","bar"),
    ORDER_AMOUNT(2, "Order Amount", "Yuan","bar"),
    ORDER_NUM(3, "Order Count", "Times","line"),
    STU_NEW_NUM(4, "New Students", "People","line"),
    CUSTOMER_UNIT_PRICE(5, "Average Order Value", "Yuan","bar"),
    STU_TOTAL_NUM(6, "Total Students", "People", "bar"),
    DAILY_LIVING_NUM(7, "Daily Active Users", "Users", "bar"),
    VISITOR_NUM(8, "Visitors", "Users", "bar"),
    PURCHASE_NUM(9, "Purchase Volume", "Times", "line"),
    NULL(null, null, null,null);

    private Integer type;
    private String name;
    private String unit;
    private String axisType;

    public static DataTypeEnum get(Integer type){
        for (DataTypeEnum dataTypeEnum : values()){
            if(dataTypeEnum.getType().equals(type)){
                return dataTypeEnum;
            }
        }
        return NULL;
    }

    public String nameWithUnit(){
        return String.format("%s (%s)", name, unit);
    }

    public static void main(String[] args) {
        System.out.println(DataTypeEnum.ORDER_AMOUNT.nameWithUnit());
    }
}
