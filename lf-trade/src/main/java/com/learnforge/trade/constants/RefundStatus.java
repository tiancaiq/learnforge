package com.learnforge.trade.constants;

import com.learnforge.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RefundStatus implements BaseEnum {


    UN_APPROVE(1, "Pending Approval", "Submit Refund Application"),
    CANCEL(2, "Student Canceled Refund", "Student Canceled Refund"),
    AGREE(3, "Agree to Refund", "Refund Approval"),
    REJECT(4, "Reject Refund", "Refund Approval"),
    SUCCESS(5, "Refund Successful", "Refund Successful"),
    FAILED(6, "Refund Failed", "Refund Failed");

    private final int value;
    private final String desc;
    private final String progressName;

    public static RefundStatus of(Integer value){
        if(value == null){
            return null;
        }
        for (RefundStatus status : values()) {
            if(status.equalsValue(value)){
                return status;
            }
        }
        return null;
    }

    public static String desc(Integer value) {
        RefundStatus status = of(value);
        if (status == null) {
            return null;
        }
        return status.getDesc();
    }

    public static String progress(Integer value) {
        RefundStatus status = of(value);
        if (status == null) {
            return null;
        }
        return status.getProgressName();
    }

    public static boolean inProgress(Integer value) {
        if (value == null) {
            return false;
        }
        return UN_APPROVE.getValue() == value || AGREE.getValue() == value;
    }
}
