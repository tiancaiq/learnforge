package com.learnforge.message.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.message.config.MessageProperties;
import com.learnforge.message.domain.dto.UserInboxDTO;
import com.learnforge.message.domain.dto.UserInboxFormDTO;
import com.learnforge.message.domain.po.NoticeTemplate;
import com.learnforge.message.domain.po.PublicNotice;
import com.learnforge.message.domain.po.UserInbox;
import com.learnforge.message.domain.query.UserInboxQuery;
import com.learnforge.message.enums.NoticeType;
import com.learnforge.message.mapper.UserInboxMapper;
import com.learnforge.message.service.IPublicNoticeService;
import com.learnforge.message.service.IUserInboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * User notification record service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Service
@RequiredArgsConstructor
public class UserInboxServiceImpl extends ServiceImpl<UserInboxMapper, UserInbox> implements IUserInboxService {

    private final MessageProperties properties;
    private final IPublicNoticeService publicNoticeService;

    @Override
    public void saveNoticeToInbox(NoticeTemplate notice, List<UserDTO> users) {
        LocalDateTime pushTime = LocalDateTime.now();
        LocalDateTime expireTime = pushTime.plusMonths(properties.getMessageTtlMonths());
        // 1. Initialize mailbox data
        List<UserInbox> list = new ArrayList<>(users.size());
        // 2. Assemble
        for (UserDTO user : users) {
            UserInbox box = new UserInbox();
            box.setTitle(notice.getTitle());
            box.setContent(notice.getContent());
            box.setUserId(user.getId());
            box.setType(notice.getType());
            box.setPushTime(pushTime);
            box.setExpireTime(expireTime);
            list.add(box);
        }
        // 3. Save
        saveBatch(list);
    }

    @Override
    @Transactional
    public PageDTO<UserInboxDTO> queryUserInBoxesPage(UserInboxQuery query) {
        // 1. Get User Information
        Long userId = UserContext.getUser();
        // 2. Query the last announcement in user mailbox to confirm the earliest time point of the current load
        UserInbox latest = getBaseMapper().queryLatestPublicNotice(userId);
        // 2.1. Default time point is current time minus the maximum validity period of announcement (earliest announcement time that has not expired)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minTime = now.minusMonths(properties.getNoticeTtlMonths());
        // 2.2. If there is the last announcement, check if the announcement time is later than the earliest time
        if(latest != null && latest.getPushTime().isAfter(minTime)){
            // If the user's last load time is later than the earliest time, update the time
            minTime = latest.getPushTime();
        }
        // 3. Sort by release time in descending order, check messages in the announcement box, up to 200 messages
        Page<PublicNotice> page = new Page<PublicNotice>(1, 200)
                .addOrder(new OrderItem("push_time", false));
        page = publicNoticeService.lambdaQuery()
                .ge(PublicNotice::getPushTime, minTime)
                .page(page);
        // 4. Write the announcement into the user's mailbox
        if (CollUtils.isNotEmpty(page.getRecords())) {
            saveNoticeListToInbox(page.getRecords(), userId);
        }
        // 5. Page query mailbox information and return
        Page<UserInbox> userInboxPage = query.toMpPage("push_time", false);
        userInboxPage = lambdaQuery()
                .eq(UserInbox::getUserId, userId)
                .eq(query.getIsRead() != null, UserInbox::getIsRead, query.getIsRead())
                .eq(query.getType() != null, UserInbox::getType, query.getType())
                .page(userInboxPage);
        return PageDTO.of(userInboxPage, UserInboxDTO.class);
    }

    private void saveNoticeListToInbox(List<PublicNotice> notices, Long userId) {
        List<UserInbox> list = new ArrayList<>(notices.size());
        for (PublicNotice notice : notices) {
            UserInbox box = new UserInbox();
            box.setTitle(notice.getTitle());
            box.setContent(notice.getContent());
            box.setUserId(userId);
            box.setType(notice.getType());
            box.setPushTime(notice.getPushTime());
            box.setExpireTime(notice.getExpireTime());
            list.add(box);
        }
        saveBatch(list);
    }

    @Override
    public Long sentMessageToUser(UserInboxFormDTO userInboxFormDTO) {
        // 1. Calculate time
        LocalDateTime pushTime = LocalDateTime.now();
        LocalDateTime expireTime = pushTime.plusMonths(properties.getMessageTtlMonths());
        // 2. Get current user
        Long userId = UserContext.getUser();
        // 3. Organize data
        UserInbox inbox = new UserInbox();
        inbox.setUserId(userInboxFormDTO.getUserId());
        inbox.setContent(userInboxFormDTO.getContent());
        inbox.setType(NoticeType.PRIVATE_MESSAGE.getValue());
        inbox.setPushTime(pushTime);
        inbox.setExpireTime(expireTime);
        inbox.setPublisher(userId);
        save(inbox);
        return inbox.getId();
    }
}
