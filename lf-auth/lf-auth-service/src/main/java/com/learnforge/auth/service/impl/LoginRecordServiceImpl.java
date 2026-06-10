package com.learnforge.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.auth.domain.po.LoginRecord;
import com.learnforge.auth.mapper.LoginRecordMapper;
import com.learnforge.auth.service.ILoginRecordService;
import com.learnforge.common.utils.MarkedRunnable;
import com.learnforge.common.utils.WebUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * Login Info Record Table Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
@Service
public class LoginRecordServiceImpl extends ServiceImpl<LoginRecordMapper, LoginRecord> implements ILoginRecordService {

    private static final Executor WRITE_RECORD_EXECUTOR;

    static {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //Configure Core Thread Count
        executor.setCorePoolSize(20);
        //Configure Maximum Thread Count
        executor.setMaxPoolSize(40);
        //Configure Queue Size
        executor.setQueueCapacity(99999);
        //Configure Thread Name Prefix in Thread Pool
        executor.setThreadNamePrefix("login-record-write-worker-");
        // Set Rejection Strategy: Discard Task
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        //Execute Initialization
        executor.initialize();
        WRITE_RECORD_EXECUTOR = executor;
    }

    @Override
    public void saveAsync(LoginRecord record) {
        WRITE_RECORD_EXECUTOR.execute(new MarkedRunnable(() -> save(record)));
    }

    @Override
    public void loginSuccess(String cellphone, Long userId) {
        LoginRecord record = new LoginRecord();
        LocalDateTime now = LocalDateTime.now();
        record.setLoginTime(now);
        record.setLoginDate(now.toLocalDate());
        record.setUserId(userId);
        record.setCellPhone(cellphone);
        record.setIpv4(WebUtils.getRemoteAddr());
        saveAsync(record);
    }
}
