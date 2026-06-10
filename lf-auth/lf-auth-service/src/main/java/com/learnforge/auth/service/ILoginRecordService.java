package com.learnforge.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.auth.domain.po.LoginRecord;

/**
 * <p>
 * Login Info Record Table Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
public interface ILoginRecordService extends IService<LoginRecord> {

    void saveAsync(LoginRecord record);

    void loginSuccess(String cellphone, Long userId);
}
