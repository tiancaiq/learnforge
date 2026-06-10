package com.learnforge.learning.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.learning.domain.dto.QuestionFormDTO;
import com.learnforge.learning.domain.query.QuestionAdminPageQuery;
import com.learnforge.learning.domain.query.QuestionPageQuery;
import com.learnforge.learning.domain.vo.QuestionAdminVO;
import com.learnforge.learning.domain.vo.QuestionVO;
import com.learnforge.learning.service.IInteractionQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-25
 */
@RestController
@RequestMapping("/admin/questions")
@Api(tags = "qa api")
@RequiredArgsConstructor
public class InteractionQuestionAdminController {

    private final IInteractionQuestionService questionService;


    @ApiOperation("amdin query by page")
    @GetMapping("page")
    public PageDTO<QuestionAdminVO> queryQuestionPageAdmin(QuestionAdminPageQuery query){
        return questionService.queryQuestionPageAdmin(query);

    }

}
