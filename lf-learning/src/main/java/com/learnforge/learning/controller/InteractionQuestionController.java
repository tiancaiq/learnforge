package com.learnforge.learning.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.learning.domain.dto.QuestionFormDTO;
import com.learnforge.learning.domain.query.QuestionPageQuery;
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
@RequestMapping("/questions")
@Api(tags = "qa api")
@RequiredArgsConstructor
public class InteractionQuestionController {

    private final IInteractionQuestionService questionService;


    @ApiOperation("new qa")
    @PostMapping
    public void saveQuestion(@Valid @RequestBody QuestionFormDTO questionDTO){
        questionService.saveQuestion(questionDTO);
    }

    @GetMapping("page")
    public PageDTO<QuestionVO> queryQuestionPage(QuestionPageQuery query){
        return questionService.queryQuestionPage(query);

    }
    @ApiOperation("query by id")
    @GetMapping("{id}")
    public QuestionVO queryQuestionById(@PathVariable Long id){
        return questionService.queryQuestionById(id);

    }

}
