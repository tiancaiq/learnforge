package com.learnforge.data.service.impl;

import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.JsonUtils;
import com.learnforge.data.constants.RedisConstants;
import com.learnforge.data.model.dto.Top10DataSetDTO;
import com.learnforge.data.model.po.CourseInfo;
import com.learnforge.data.model.vo.Top10DataVO;
import com.learnforge.data.service.Top10Service;
import com.learnforge.data.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName Top10ServiceImpl
 * @Author wusongsong
 * @Date 2022/10/10 19:46
 * @Version
 **/
@Service
public class Top10ServiceImpl implements Top10Service {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public Top10DataVO getTop10Data() {
        // 1. Data redis storage key
        String key = RedisConstants.KEY_TOP10 + DataUtils.getVersion(1);
        // 2. Get data
        Object originData = redisTemplate.opsForValue().get(key);
        // 2.1. Data Null Check
        if (originData == null) {
            return new Top10DataVO();
        }
        // 3. Convert data into course information
        List<CourseInfo> data = JsonUtils.toList(originData.toString(), CourseInfo.class);
        // 4. Data Assembly
        Top10DataVO top10DataVO = new Top10DataVO();
        // 4.1. Set popular courses
        top10DataVO.setHot(data.stream()
                .sorted(Comparator.comparing(CourseInfo::getNewStuNum).reversed())
                .limit(10)
                .collect(Collectors.toList()));
        // 4.2. Set best-selling courses
        top10DataVO.setHotSales(data.stream()
                .sorted(Comparator.comparing(CourseInfo::getOrderAmount).reversed())
                .limit(10)
                .collect(Collectors.toList()));
        return top10DataVO;
    }

    @Override
    public void setTop10Data(Top10DataSetDTO top10DataSetDTO) {
        // 1. Data redis storage key
        String key = RedisConstants.KEY_TOP10 + top10DataSetDTO.getVersion();
        // 2. Data conversion
        List<CourseInfo> courseInfoList = BeanUtils.copyList(top10DataSetDTO.getData(), CourseInfo.class);

        //3. Add or reset data
        redisTemplate.opsForValue().set(
                key,
                JsonUtils.toJsonStr(courseInfoList)
        );
    }
}
