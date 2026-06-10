package com.learnforge.remark.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.learnforge.api.dto.remark.LikeTimesDTO;
import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.remark.domain.dto.LikeRecordFormDTO;
import com.learnforge.remark.domain.po.LikedRecord;
import com.learnforge.remark.mapper.LikedRecordMapper;
import com.learnforge.remark.service.ILikedRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
//@Service
public class LikedRecordServiceImpl extends ServiceImpl<LikedRecordMapper, LikedRecord> implements ILikedRecordService {

    private final RabbitMqHelper mqHelper;

    @Override
    public void addLikeRecord(LikeRecordFormDTO recordDTO) {
        // 1. like or cancel
        boolean success =  recordDTO.getLiked() ? like(recordDTO) : unlike(recordDTO);
        // 2. successed, if fail then end
        if (!success) {
            return;
        }
        //3. successs get total like
        Integer likeTimes = lambdaQuery()
                .eq(LikedRecord::getBizId, recordDTO.getBizId())
                .count();
        //4. MQ
        mqHelper.send(
                LIKE_RECORD_EXCHANGE,
                StringUtils.format(LIKED_TIMES_KEY_TEMPLATE,recordDTO.getBizType()),
                LikeTimesDTO.of(recordDTO.getBizId(),likeTimes));

    }

    @Override
    public Set<Long> isBizLiked(List<Long> bizIds) {
        // get current user
        Long userId = UserContext.getUser();
        // query like
        List<LikedRecord> list = lambdaQuery()
                .in(LikedRecord::getBizId, bizIds)
                .eq(LikedRecord::getUserId, userId)
                .list();
        return list.stream().map(LikedRecord::getBizId).collect(Collectors.toSet());
    }

    @Override
    public void readLikedTimesAndSendMessage(String bizType, int maxBizSize) {

    }


    private boolean unlike(LikeRecordFormDTO recordDTO) {



        return remove(new QueryWrapper<LikedRecord>().lambda()
                .eq(LikedRecord::getUserId, UserContext.getUser())
                .eq(LikedRecord::getBizId, recordDTO.getBizId()));
    }

    private boolean like(LikeRecordFormDTO recordDTO) {

        Integer count = lambdaQuery()
                .eq(LikedRecord::getUserId, UserContext.getUser())
                .eq(LikedRecord::getBizId, recordDTO.getBizId())
                .count();
        if (count > 0) {
            return false;
        }
        LikedRecord r = new LikedRecord();
        r.setUserId(UserContext.getUser());
        r.setBizId(recordDTO.getBizId());
        r.setBizType(recordDTO.getBizType());
        save(r);
        return true;
    }
}
