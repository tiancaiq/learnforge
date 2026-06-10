package com.learnforge.common.constants;

public interface Constant {
    String REQUEST_ID_HEADER = "requestId";
    String REQUEST_FROM_HEADER = "x-request-from";

    String GATEWAY_ORIGIN_NAME = "gateway";
    String FEIGN_ORIGIN_NAME = "feign";

    // Data field - id
    String DATA_FIELD_NAME_ID = "id";

    // Data field - create_time
    String DATA_FIELD_NAME_CREATE_TIME = "create_time";
    String DATA_FIELD_NAME_CREATE_TIME_CAMEL = "createTime";

    // Data field - update_time
    String DATA_FIELD_NAME_UPDATE_TIME = "update_time";
    String DATA_FIELD_NAME_UPDATE_TIME_CAMEL = "updateTime";

    // Data field - liked_times
    String DATA_FIELD_NAME_LIKED_TIME = "liked_times";
    String DATA_FIELD_NAME_LIKED_TIME_CAMEL = "likedTimes";

    // Data field - creater
    String DATA_FIELD_NAME_CREATER = "creater";

    // Data field - updater
    String DATA_FIELD_NAME_UPDATER = "updater";

    // Data deletion flag value
    boolean DATA_DELETE = true;
    // Data not deleted flag value
    boolean DATA_NOT_DELETE = false;
    // Response result whether marked by R
    String BODY_PROCESSED_MARK_HEADER = "IS_BODY_PROCESSED";





}
