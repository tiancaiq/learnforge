package com.learnforge.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learnforge.message.domain.po.UserInbox;

/**
 * <p>
 * User notification record Mapper interface
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
public interface UserInboxMapper extends BaseMapper<UserInbox> {

    UserInbox queryLatestPublicNotice(Long userId);
}
