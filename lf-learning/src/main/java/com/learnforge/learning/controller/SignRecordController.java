package com.learnforge.learning.controller;


import com.learnforge.learning.domain.po.LearningLesson;
import com.learnforge.learning.domain.vo.SignResultVO;
import com.learnforge.learning.service.ISignRecordService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "check in api")
@RestController
@RequiredArgsConstructor
@RequestMapping("sign-records")
public class SignRecordController {

    private final ISignRecordService recordService;

    @PostMapping
    public SignResultVO addSignRecord() {
        return recordService.addSignRecords();
    }
}
