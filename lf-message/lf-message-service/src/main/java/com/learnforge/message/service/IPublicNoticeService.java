package com.learnforge.message.service;

import com.learnforge.message.domain.po.NoticeTemplate;
import com.learnforge.message.domain.po.PublicNotice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * Announcement message template service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
public interface IPublicNoticeService extends IService<PublicNotice> {

    void saveNoticeOfTemplate(NoticeTemplate noticeTemplate);
}
