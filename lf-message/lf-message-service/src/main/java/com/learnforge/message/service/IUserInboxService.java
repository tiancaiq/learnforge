package com.learnforge.message.service;

import com.learnforge.message.domain.dto.UserInboxDTO;
import com.learnforge.message.domain.dto.UserInboxFormDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.message.domain.query.UserInboxQuery;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.message.domain.po.NoticeTemplate;
import com.learnforge.message.domain.po.UserInbox;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * User notification record service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
public interface IUserInboxService extends IService<UserInbox> {

    void saveNoticeToInbox(NoticeTemplate noticeTemplate, List<UserDTO> users);

    PageDTO<UserInboxDTO> queryUserInBoxesPage(UserInboxQuery query);

    Long sentMessageToUser(UserInboxFormDTO userInboxFormDTO);
}
