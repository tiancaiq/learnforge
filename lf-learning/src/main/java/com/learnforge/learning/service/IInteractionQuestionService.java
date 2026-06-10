package com.learnforge.learning.service;

import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.learning.domain.dto.QuestionFormDTO;
import com.learnforge.learning.domain.po.InteractionQuestion;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.learning.domain.query.QuestionAdminPageQuery;
import com.learnforge.learning.domain.query.QuestionPageQuery;
import com.learnforge.learning.domain.vo.QuestionAdminVO;
import com.learnforge.learning.domain.vo.QuestionVO;

import javax.validation.Valid;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-25
 */
public interface IInteractionQuestionService extends IService<InteractionQuestion> {

    void saveQuestion(@Valid QuestionFormDTO questionDTO);

    PageDTO<QuestionVO> queryQuestionPage(QuestionPageQuery query);

    QuestionVO queryQuestionById(Long id);

    PageDTO<QuestionAdminVO> queryQuestionPageAdmin(QuestionAdminPageQuery query);
}
