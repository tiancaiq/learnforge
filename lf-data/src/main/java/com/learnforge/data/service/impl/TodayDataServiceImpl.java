package com.learnforge.data.service.impl;

import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.JsonUtils;
import com.learnforge.data.constants.RedisConstants;
import com.learnforge.data.model.dto.TodayDataDTO;
import com.learnforge.data.model.po.TodayDataInfo;
import com.learnforge.data.model.vo.TodayDataVO;
import com.learnforge.data.service.TodayDataService;
import com.learnforge.data.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @ClassName TodayDataServiceImpl
 * @Author wusongsong
 * @Date 2022/10/13 9:28
 * @Version
 **/
@Service
public class TodayDataServiceImpl implements TodayDataService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public TodayDataVO get() {
        // 1. Data redis storage key
        String key = RedisConstants.KEY_TODAY + DataUtils.getVersion(1);
        // 2. Get data
        Object originData = redisTemplate.opsForValue().get(key);
        // 2.1. Data Null Check
        if (originData == null) {
            return new TodayDataVO();
        }
        return JsonUtils.toBean(originData.toString(), TodayDataVO.class);
    }

    @Override
    public void set(TodayDataDTO todayDataDTO) {
        // 1. Data redis storage key
        String key = RedisConstants.KEY_TODAY + todayDataDTO.getVersion();
        // 2. Data conversion
        TodayDataInfo todayDataInfo = BeanUtils.toBean(todayDataDTO, TodayDataInfo.class);
        // 3. Data storage
        redisTemplate.opsForValue().set(key, JsonUtils.toJsonStr(todayDataInfo));
    }
}
