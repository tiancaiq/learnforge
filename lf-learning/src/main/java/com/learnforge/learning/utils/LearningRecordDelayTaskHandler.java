package com.learnforge.learning.utils;

import com.learnforge.common.utils.JsonUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.learning.domain.po.LearningLesson;
import com.learnforge.learning.domain.po.LearningRecord;
import com.learnforge.learning.mapper.LearningRecordMapper;
import com.learnforge.learning.service.ILearningLessonService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.DelayQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class LearningRecordDelayTaskHandler {

    private final StringRedisTemplate redisTemplate;
    private final LearningRecordMapper recordMapper;
    private final ILearningLessonService lessonService;
    private final DelayQueue<DelayTask<RecordTaskData>> queue = new DelayQueue<>();
    private final static String RECORD_KEY_TEMPLATE = "learning:record:{}";
    private static volatile boolean begin = true;
    // initinalize async at start
    @PostConstruct
    public void init(){
        CompletableFuture.runAsync(this::handleDelayTask);
    }
    //Destroy while at end.
    @PreDestroy
    public void destory(){
        begin = false;
        log.debug("delay task stoped");
    }
    public void handleDelayTask(){
        while (begin){
            try {
                // 1get expired task
                DelayTask<RecordTaskData> task = queue.take();
                RecordTaskData data = task.getData();
                // 2. query redis
                LearningRecord record = readRecordCache(data.getLessonId(), data.sectionId);
                if (record == null) {
                    continue;
                }
                //3. compare moment
                if (Objects.equals(data.getMoment(), record.getMoment())) {
                    // not same drop old data
                    continue;
                }

                //4 same update  to db

                //4.1 update record moment
                record.setFinished(null);
                recordMapper.updateById(record);
                //4.2 update lesson info
                LearningLesson lesson = new LearningLesson();
                lesson.setId(data.getLessonId());
                lesson.setLatestSectionId(data.getSectionId());
                lesson.setLatestLearnTime(LocalDateTime.now());
                lessonService.updateById(lesson);

            } catch (Exception e) {
                log.error("deal delay task error",e);
            }
        }
    }

    public void addLearningRecord(LearningRecord record){
        //1. add data to redis
        writeRecordCache(record);
        //2. sumbit to delayQueue

        queue.add(new DelayTask<>(new RecordTaskData(record),Duration.ofSeconds(20)));
    }

    public void writeRecordCache(LearningRecord record) {

        log.debug("update learning record cache data");
        //1. convert data
        try {
            String json = JsonUtils.toJsonStr(new RecordCacheData(record));
            //2. redis
            String key = StringUtils.format(RECORD_KEY_TEMPLATE, record.getLessonId());
            redisTemplate.opsForHash().put(key,record.getSectionId().toString(),json);
            //3 add expire time
            redisTemplate.expire(key, Duration.ofMinutes(1));
        } catch (Exception e) {
            log.error("write record cache error",e);
        }
    }

    public LearningRecord readRecordCache( Long lessonId, Long sectionId){
        // read redis
        try {
            String key = StringUtils.format(RECORD_KEY_TEMPLATE, lessonId);
            Object cacheData = redisTemplate.opsForHash().get(key, sectionId.toString());

            if (cacheData == null){
                return null;
            }

            // 2. check and convert
            return JsonUtils.toBean(cacheData.toString(),LearningRecord.class);
        } catch (Exception e) {
            log.error("read record cache error",e);
            return null;
        }
    }


    public void cleanRecordCache(Long lessonId, Long sectionId){
        String key = StringUtils.format(RECORD_KEY_TEMPLATE, lessonId);
        redisTemplate.opsForHash().delete(key,sectionId.toString());
    }


    @Data
    @NoArgsConstructor
    private static class RecordCacheData{
        private Long id;
        private Integer moment;
        private Boolean finished;

        public RecordCacheData(LearningRecord record) {
            this.id = record.getId();
            this.moment = record.getMoment();
            this.finished = record.getFinished();
        }
    }

    @Data
    @NoArgsConstructor
    private static class RecordTaskData{
        private Long lessonId;
        private Long sectionId;
        private Integer moment;

        public RecordTaskData(LearningRecord record) {
            this.lessonId = record.getLessonId();
            this.sectionId = record.getSectionId();
            this.moment = record.getMoment();
        }
    }


}
