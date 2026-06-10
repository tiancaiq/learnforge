package com.learnforge.remark.task;

import com.learnforge.remark.service.ILikedRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LikedTimesCheckTask {

    private static final List<String> BIZ_TYPES = List.of("QA","NOTE");

    private final ILikedRecordService recordService;

    private static final int MAX_BIZ_SIZE = 30;

    @Scheduled(fixedDelay = 20000)
    public void chekLikedTimes()
    {
        for (String bizType : BIZ_TYPES) {
            recordService.readLikedTimesAndSendMessage(bizType,MAX_BIZ_SIZE);
        }
    }
}
