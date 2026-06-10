package com.learnforge.remark.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.dto.remark.LikeTimesDTO;
import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.remark.domain.dto.LikeRecordFormDTO;
import com.learnforge.remark.domain.po.LikedRecord;
import com.learnforge.remark.mapper.LikedRecordMapper;
import com.learnforge.remark.service.ILikedRecordService;
import com.learnforge.remark.constants.RedisConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.learnforge.common.constants.MqConstants.Exchange.LIKE_RECORD_EXCHANGE;
import static com.learnforge.common.constants.MqConstants.Key.LIKED_TIMES_KEY_TEMPLATE;

/**
 * <p>
 * Like Record Table Service Implementation Class
 * </p>
 *
 * @author luke
 * @since 2026-05-26
 */

@RequiredArgsConstructor
@Service
public class LikedRecordServiceRedisImpl extends ServiceImpl<LikedRecordMapper, LikedRecord> implements ILikedRecordService {

    private final RabbitMqHelper mqHelper;
    private final StringRedisTemplate redisTemplate;
    @Override
    public void addLikeRecord(LikeRecordFormDTO recordDTO) {
        // 1. like or cancel
        boolean success =  recordDTO.getLiked() ? like(recordDTO) : unlike(recordDTO);
        // 2. successed, if fail then end
        if (!success) {
            return;
        }
        //3. successs get total like
        Long likeTimes = redisTemplate.opsForSet()
                .size(RedisConstants.LIKES_BIZ_KEY_PREFIX + recordDTO.getBizId());
        if (likeTimes == null) {
            return;
        }
        //4. redis
        redisTemplate.opsForZSet().add(
                RedisConstants.LIKES_TIMES_KEY_PREFIX + recordDTO.getBizType(),
                recordDTO.getBizId().toString(),
                likeTimes
        );
    }

    @Override
    public Set<Long> isBizLiked(List<Long> bizIds) {
        // get current user
        Long userId = UserContext.getUser();
        // query like

        List<Object> objects = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection scr = (StringRedisConnection) connection;
            for (Long bizId : bizIds) {
                String key = RedisConstants.LIKES_BIZ_KEY_PREFIX + bizId;
                scr.sIsMember(key, userId.toString());
            }

            return null;
        });
        Set<Long> set = new HashSet<>();
        for (int i = 0; i < objects.size(); i++) {
            Boolean o = (Boolean) objects.get(i);
            if (o) {
                set.add(bizIds.get(i));
            }
        }

        return set;
    }

    @Override
    public void readLikedTimesAndSendMessage(String bizType, int maxBizSize) {
        // 1.read and remove redis total likes
        String key = RedisConstants.LIKES_TIMES_KEY_PREFIX + bizType;
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().popMin(key, maxBizSize);
        if (CollUtils.isEmpty(tuples)) {
            return;
        }
        // 2. data convert
        List <LikeTimesDTO> list = new ArrayList<>(tuples.size());
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            String bizId = tuple.getValue();
            Double likedTimes = tuple.getScore();
            if (likedTimes == null || bizId == null) {
                continue;
            }
            list.add(LikeTimesDTO.of(Long.valueOf(bizId), likedTimes.intValue()));
        }
        // 3. send MQ
        mqHelper.send(
                LIKE_RECORD_EXCHANGE,
                StringUtils.format(LIKED_TIMES_KEY_TEMPLATE,bizType),
                list);
    }


    private boolean unlike(LikeRecordFormDTO recordDTO) {


        Long userId = UserContext.getUser();
        String key = RedisConstants.LIKES_BIZ_KEY_PREFIX + recordDTO.getBizId();
        Long result = redisTemplate.opsForSet().remove(key, userId.toString());
        return result != null && result > 0;
    }

    private boolean like(LikeRecordFormDTO recordDTO) {
        Long userId = UserContext.getUser();
        String key = RedisConstants.LIKES_BIZ_KEY_PREFIX + recordDTO.getBizId();
        Long result = redisTemplate.opsForSet().add(key, userId.toString());
        return result != null && result > 0;
    }
}
