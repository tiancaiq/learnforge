package com.learnforge.learning.controller;


import com.learnforge.api.dto.leanring.LearningLessonDTO;
import com.learnforge.learning.domain.dto.LearningRecordFormDTO;
import com.learnforge.learning.service.ILearningRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-14
 */
@RestController
@RequestMapping("/learning-records")
@Api(tags = "recoring api")
@RequiredArgsConstructor
public class LearningRecordController {

    private final ILearningRecordService recordService;

    @ApiOperation("query specific learning record")
    @GetMapping("course/{courseId}")
    LearningLessonDTO queryLearningRecordByCourse(
            @ApiParam(value = "course id", example =  "2") @PathVariable("courseId") Long courseId){
        return recordService.queryLearningRecordByCourse(courseId);
    }


    @PostMapping
    @ApiOperation("submit record")
    public void addLearningRecord(@RequestBody LearningRecordFormDTO formDTO){
        recordService.addLearningRecord(formDTO);

    }

}
