package com.learnforge.media.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.learnforge.common.exceptions.BadRequestException;

import static com.learnforge.media.enums.FileErrorInfo.Msg.INVALID_FILE_STATUS;


public enum FileStatus {
    UPLOADING(1, "Uploading"),
    UPLOADED(2, "Uploaded"),
    PROCESSED(3, "Processed"),
    ;
    @EnumValue
    private int value;
    private String desc;

    FileStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static FileStatus of(int value) {
        switch (value) {
            case 1:
                return UPLOADING;
            case 2:
                return UPLOADED;
            case 3:
                return PROCESSED;
            default:
                throw new BadRequestException(INVALID_FILE_STATUS);
        }
    }
}
