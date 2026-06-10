package com.learnforge.learning.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.DateUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.learning.constants.RedisConstants;
import com.learnforge.learning.domain.enums.PointsRecordType;
import com.learnforge.learning.domain.po.PointsRecord;
import com.learnforge.learning.domain.vo.PointsStatisticsVO;
import com.learnforge.learning.mapper.PointsRecordMapper;
import com.learnforge.learning.service.IPointsRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-28
 */
@Service
@RequiredArgsConstructor
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements IPointsRecordService {


    private final StringRedisTemplate redisTemplate;

    @Override
    public void addPointsRecord(Long userId, int points, PointsRecordType recordType) {
        // check if have max

        LocalDateTime now = LocalDateTime.now();
        int maxPoints = recordType.getMaxPoints();
        int realPoints = points;
        if (maxPoints > 0) {
            //2. yes

            LocalDateTime begin = DateUtils.getDayStartTime(now);
            LocalDateTime end = DateUtils.getDayEndTime(now);
            //2.1 query today
            int currentPoints = queryUserPointsByTypeAndDate(userId, recordType,begin,end);
            //2.2 reach max?
            if (currentPoints >= maxPoints) {return;}
            // 2.3 reached end

            //2.4 no save

            if(currentPoints +  points> maxPoints){
                realPoints = maxPoints - currentPoints;
            }
        }


        // 3. no, save
        PointsRecord p = new PointsRecord();
        p.setUserId(userId);
        p.setPoints(realPoints);
        p.setType(recordType);
        save(p);

        // 4. update to sortedset
        String key = RedisConstants.POINTS_BOARD_KEY_PREFIX + now.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);
        redisTemplate.opsForZSet().incrementScore(key,userId.toString(),realPoints);
    }

    @Override
    public List<PointsStatisticsVO> queryMyPointsToday() {

        Long userId = UserContext.getUser();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = DateUtils.getDayStartTime(now);
        LocalDateTime end = DateUtils.getDayEndTime(now);

        QueryWrapper<PointsRecord> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(PointsRecord::getUserId, userId)
                .between(PointsRecord::getCreateTime,begin,end);

        List<PointsRecord> list = getBaseMapper().queryUserPointsDate(wrapper);
        if(CollUtils.isEmpty(list)){return new ArrayList<>();}

        List<PointsStatisticsVO> vos = new ArrayList<>(list.size());

        for (PointsRecord p : list) {
            PointsStatisticsVO vo = new PointsStatisticsVO();
            vo.setMaxPoints(p.getType().getMaxPoints());
            vo.setPoints(p.getPoints());
            vo.setType(p.getType().getDesc());
            vos.add(vo);
        }
        return vos;
    }

    private int queryUserPointsByTypeAndDate(Long userId, PointsRecordType recordType, LocalDateTime begin, LocalDateTime end) {

        QueryWrapper<PointsRecord> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(PointsRecord::getUserId,userId)
                .eq(PointsRecord::getType,recordType)
                .between(PointsRecord::getCreateTime,begin,end);

        Integer points =  getBaseMapper().queryUserPointsByTypeAndDate(wrapper);
        return points == null ? 0 : points;
    }
}
