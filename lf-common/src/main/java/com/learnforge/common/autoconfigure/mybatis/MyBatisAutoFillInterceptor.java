package com.learnforge.common.autoconfigure.mybatis;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.learnforge.common.utils.ReflectUtils;
import com.learnforge.common.utils.UserContext;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.sql.SQLException;

import static com.learnforge.common.constants.Constant.DATA_FIELD_NAME_CREATER;
import static com.learnforge.common.constants.Constant.DATA_FIELD_NAME_UPDATER;

public class MyBatisAutoFillInterceptor implements InnerInterceptor {

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        //1. Update operation
        updateExe(parameter);
        //2. Insert operation
        insertExe(ms, parameter);
    }

    private void insertExe(MappedStatement ms, Object parameter){
        //1. Determine if the current operation is an insert operation
        if(ms.getSqlCommandType().compareTo(SqlCommandType.INSERT) == 0) {
            //2. Check if there is an updater field, if
            if(ReflectUtils.containField(DATA_FIELD_NAME_CREATER, parameter.getClass())){
                Long userId = UserContext.getUser();
                //3. There is userId also exists and set updater
                if(userId != null){
                    //4. Set current operator to the creator field
                    ReflectUtils.setFieldValue(parameter, DATA_FIELD_NAME_CREATER, userId);
                }
            }
        }
    }

    private void updateExe(Object parameter){
        //1. Check if there is an updater field
        if(ReflectUtils.containField(DATA_FIELD_NAME_UPDATER, parameter.getClass())){
            Long userId = UserContext.getUser();
            //2. If there is userId also exists and set updater
            if(userId != null){
                //3. Set current user to the updater field
                ReflectUtils.setFieldValue(parameter, DATA_FIELD_NAME_UPDATER, userId);
            }
        }
    }
}
