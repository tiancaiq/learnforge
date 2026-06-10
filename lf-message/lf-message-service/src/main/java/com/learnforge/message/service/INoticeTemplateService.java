package com.learnforge.message.service;

import com.learnforge.message.domain.dto.NoticeTemplateDTO;
import com.learnforge.message.domain.dto.NoticeTemplateFormDTO;
import com.learnforge.message.domain.query.NoticeTemplatePageQuery;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.message.domain.po.NoticeTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * Notification template service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
public interface INoticeTemplateService extends IService<NoticeTemplate> {

    Long saveNoticeTemplate(NoticeTemplateFormDTO noticeTemplateFormDTO);

    void updateNoticeTemplate(NoticeTemplateFormDTO noticeTemplateFormDTO);

    PageDTO<NoticeTemplateDTO> queryNoticeTemplates(NoticeTemplatePageQuery pageQuery);

    NoticeTemplateDTO queryNoticeTemplate(Long id);

    NoticeTemplate queryByCode(String code);
}
