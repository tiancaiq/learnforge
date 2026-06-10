package com.learnforge.common.autoconfigure.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.learnforge.common.utils.NumberUtils;
import com.learnforge.common.utils.UserContext;
import org.apache.ibatis.reflection.MetaObject;

import static com.learnforge.common.constants.Constant.DATA_FIELD_NAME_CREATER;
import static com.learnforge.common.constants.Constant.DATA_FIELD_NAME_UPDATER;


/**
 * Automatically fill in content to be updated before database operation, only supports single object, does not support batch insert/update filling
 *
 **/
public class BaseMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        //Creator
        setCreater(metaObject);

        //Updater
        setUpdater(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //Update updater when updating data
        setUpdater(metaObject);
    }

    private void setCreater(MetaObject metaObject) {
        Long userId = UserContext.getUser();
        //User id not found, default 0
        this.strictInsertFill(metaObject, DATA_FIELD_NAME_CREATER, Long.class, NumberUtils.null2Zero(userId)); // Start version 3.3.0 (recommended use)
    }

    private void setUpdater(MetaObject metaObject) {
        Long userId = UserContext.getUser();
        //User id not found, default 0
        this.strictUpdateFill(metaObject, DATA_FIELD_NAME_UPDATER, Long.class, NumberUtils.null2Zero(userId)); // Start version 3.3.0 (recommended)
    }
}
