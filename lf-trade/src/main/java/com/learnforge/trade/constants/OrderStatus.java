package com.learnforge.trade.constants;

import com.learnforge.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum OrderStatus implements BaseEnum {

    NO_PAY(1, "Pending Payment", "Place order"),
    PAYED(2, "Already paid", "Pay"),
    CLOSED(3, "Closed", "Transaction Closed"),
    FINISHED(4, "Completed", "Transaction Completed"),
    ENROLLED(5, "Registered", "Free Registration"),
    REFUNDED(6, "Apply for Refund", "Apply for Refund");

    private final int value;
    private final String desc;
    private final String progressName;

    public static OrderStatus of(Integer value){
        if(value == null){
            return null;
        }
        for (OrderStatus status : values()) {
            if(status.equalsValue(value)){
                return status;
            }
        }
        return null;
    }

    public static String desc(Integer value) {
        OrderStatus status = of(value);
        if (status == null) {
            return null;
        }
        return status.getDesc();
    }

    public static String progress(Integer value) {
        OrderStatus status = of(value);
        if (status == null) {
            return null;
        }
        return status.getProgressName();
    }

    public static boolean canRefund(Integer value) {
        return PAYED.equalsValue(value);
    }
}
