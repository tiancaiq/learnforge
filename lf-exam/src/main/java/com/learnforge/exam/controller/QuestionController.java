package com.learnforge.exam.controller;


import com.learnforge.api.dto.exam.QuestionDTO;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.exam.domain.dto.QuestionFormDTO;
import com.learnforge.exam.domain.query.QuestionPageQuery;
import com.learnforge.exam.domain.vo.QuestionDetailVO;
import com.learnforge.exam.domain.vo.QuestionPageVO;
import com.learnforge.exam.service.IQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Question, frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
@Api(tags = "Question management related interfaces")
@RequiredArgsConstructor
@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final IQuestionService questionService;

    @ApiOperation("Add question")
    @PostMapping
    public void addQuestion(@Valid @RequestBody QuestionFormDTO questionDTO){
        questionService.addQuestion(questionDTO);
    }

    @ApiOperation("Modify question")
    @PutMapping("/{id}")
    public void updateQuestion(
            @ApiParam("Id of the question to be modified") @PathVariable("id") Long id,
            @RequestBody QuestionFormDTO questionDTO){
        questionDTO.setId(id);
        questionService.updateQuestion(questionDTO);
    }

    @ApiOperation("Delete question")
    @DeleteMapping("/{id}")
    public void deleteQuestionById( @ApiParam("Id of the question to be deleted") @PathVariable("id") Long id){
        questionService.deleteQuestionById(id);
    }

    @ApiOperation("Page query question")
    @GetMapping("page")
    public PageDTO<QuestionPageVO> queryQuestionByPage(QuestionPageQuery query){
        return questionService.queryQuestionByPage(query);
    }

    @ApiOperation("Query question details")
    @GetMapping("{id}")
    public QuestionDetailVO queryQuestionDetailById(@ApiParam("Id of the question to be queried") @PathVariable("id") Long id){
        return questionService.queryQuestionDetailById(id);
    }

    @ApiOperation("Query question list")
    @GetMapping("list")
    public List<QuestionDTO> queryQuestionByIds(@ApiParam("Ids of questions to be queried") @RequestParam("ids") List<Long> ids){
        return questionService.queryQuestionByIds(ids);
    }

    @ApiOperation("Query question score")
    @GetMapping("/scores")
    public Map<Long, Integer> queryQuestionScores(
            @ApiParam("collection of ids of questions to query") @RequestParam("ids") List<Long> ids){
        return questionService.queryQuestionScores(ids);
    }

    @ApiOperation("Query number of questions created by teacher")
    @GetMapping("/numOfTeacher")
    public Map<Long, Integer> countSubjectNumOfTeacher(
            @ApiParam("Collection of teachers to be queried") @RequestParam("ids") List<Long> createrIds){
        return questionService.countQuestionNumOfCreater(createrIds);
    }

    @ApiOperation("Query list of questions related to business")
    @GetMapping("listOfBiz")
    public List<QuestionDTO> queryQuestionByBizId(@ApiParam("collection of ids of questions to query") @RequestParam("bizId") Long bizId){
        return questionService.queryQuestionByBizId(bizId);
    }

    @ApiOperation("Validate if name is valid, return false if exists, return true if not exists")
    @GetMapping("/checkName")
    public Boolean checkNameValid(@RequestParam("name") String name){
        return questionService.checkNameValid(name);
    }
}
