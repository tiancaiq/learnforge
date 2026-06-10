package com.learnforge.exam.service.impl;

import com.learnforge.exam.domain.po.QuestionDetail;
import com.learnforge.exam.mapper.QuestionDetailMapper;
import com.learnforge.exam.service.IQuestionDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Question Service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
@Service
public class QuestionDetailServiceImpl extends ServiceImpl<QuestionDetailMapper, QuestionDetail> implements IQuestionDetailService {

}
