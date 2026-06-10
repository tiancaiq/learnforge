package com.learnforge.message.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.message.config.MessageProperties;
import com.learnforge.message.domain.po.NoticeTemplate;
import com.learnforge.message.domain.po.PublicNotice;
import com.learnforge.message.mapper.PublicNoticeMapper;
import com.learnforge.message.service.IPublicNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * Announcement message template service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Service
@RequiredArgsConstructor
public class PublicNoticeServiceImpl extends ServiceImpl<PublicNoticeMapper, PublicNotice> implements IPublicNoticeService {

    private final MessageProperties messageProperties;
    @Override
    public void saveNoticeOfTemplate(NoticeTemplate noticeTemplate) {
        LocalDateTime now = LocalDateTime.now();
        PublicNotice notice = new PublicNotice();
        notice.setTitle(noticeTemplate.getTitle());
        notice.setContent(noticeTemplate.getContent());
        notice.setPushTime(now);
        notice.setType(noticeTemplate.getType());
        notice.setExpireTime(now.plusMonths(messageProperties.getNoticeTtlMonths()));
        save(notice);
    }
}
