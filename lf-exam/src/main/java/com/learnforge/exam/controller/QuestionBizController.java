package com.learnforge.exam.controller;


import com.learnforge.api.dto.exam.QuestionBizDTO;
import com.learnforge.exam.service.IQuestionBizService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Question and business association table, for example, associate small section id with question id, one small section can have multiple questions, frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
@Api(tags = "Question management related interfaces")
@RestController
@RequestMapping("/question-biz")
@RequiredArgsConstructor
public class QuestionBizController {

    private final IQuestionBizService bizService;

    @ApiOperation("Batch save question and business relationship")
    @PostMapping("list")
    public void saveQuestionBizInfoBatch(@RequestBody List<QuestionBizDTO> qbs){
        bizService.saveQuestionBizInfoBatch(qbs);
    }

    @ApiOperation("Query question ids related to business")
    @GetMapping("/biz/{id}")
    public List<QuestionBizDTO> queryQuestionIdsByBizId(@ApiParam("Business id") @PathVariable("id") Long bizId){
        return bizService.queryQuestionIdsByBizId(bizId);
    }

    @ApiOperation("Batch query question ids related to business")
    @GetMapping("/biz/list")
    public List<QuestionBizDTO> queryQuestionIdsByBizIds(@ApiParam("business id collection") @RequestParam("ids") List<Long> bizIds){
        return bizService.queryQuestionIdsByBizIds(bizIds);
    }

    @ApiOperation("Query score of questions under business")
    @GetMapping("/scores")
    public Map<Long, Integer> queryQuestionScoresByBizIds(@RequestParam("ids") List<Long> bizIds){
        return bizService.queryQuestionScoresByBizIds(bizIds);
    }
}
