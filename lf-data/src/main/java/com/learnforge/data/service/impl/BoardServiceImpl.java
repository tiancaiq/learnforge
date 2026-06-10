package com.learnforge.data.service.impl;

import com.learnforge.common.utils.JsonUtils;
import com.learnforge.common.utils.NumberUtils;
import com.learnforge.data.constants.DataTypeEnum;
import com.learnforge.data.model.dto.BoardDataSetDTO;
import com.learnforge.data.model.vo.AxisVO;
import com.learnforge.data.model.vo.EchartsVO;
import com.learnforge.data.model.vo.SerierVO;
import com.learnforge.data.service.BoardService;
import com.learnforge.data.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.learnforge.data.constants.RedisConstants.KEY_BOARD_DATA;

/**
 * @ClassName BoardServiceImpl
 * @Author wusongsong
 * @Date 2022/10/10 16:32
 * @Version
 **/
@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public EchartsVO boardData(List<Integer> types) {
        // 1. Define ECharts Variables
        EchartsVO echartsVO = new EchartsVO();
        List<AxisVO> yAxis = new ArrayList<>();
        List<SerierVO> series = new ArrayList<>();
        // 2. Traverse Data Types types
        // 2.1. Data Version
        int version = DataUtils.getVersion(1);

        for (Integer type : types) {
            // 2.1. Get Data Type
            DataTypeEnum dataTypeEnum = DataTypeEnum.get(type);
            // 2.2. Get Data
            Object originData = redisTemplate.opsForHash().get(KEY_BOARD_DATA + version, type.toString());
            List<Double> data = originData == null
                    ? new ArrayList<>()
                    : JsonUtils.toList(originData.toString(), Double.class);
            // 2.3. Calculate Maximum and Minimum Values
            Double max = NumberUtils.null2Zero(NumberUtils.max(data));
            Double min = NumberUtils.null2Zero(NumberUtils.min(data));
            // 2.2. Set Data
            series.add(new SerierVO(
                    dataTypeEnum.nameWithUnit(),
                    dataTypeEnum.getAxisType(),
                    data,
                    max + dataTypeEnum.getUnit(),
                    min + dataTypeEnum.getUnit()
                    ));
            // 2.3. Set y-axis data
            yAxis.add(AxisVO.builder()
                    .max(max)
                    .min(NumberUtils.setScale(min * 0.9))
                    .interval(((int)NumberUtils.div((max - min * 0.9), 10.0) + 1) * 1.0)
                    .average(
                            NumberUtils.setScale(NumberUtils.null2Zero(NumberUtils.average(data))))
                    .type(AxisVO.TYPE_VALUE)
                    .build());
        }
        // 3. Data encapsulation
        // 3.1. x-axis data
        echartsVO.setXAxis(Collections.singletonList(AxisVO.last15Day()));
        // 3.2. y-axis data
        echartsVO.setYAxis(yAxis);
        // 3.3. series data
        echartsVO.setSeries(series);
        return echartsVO;
    }

    @Override
    public void setBoardData(BoardDataSetDTO boardDataSetDTO) {
        String key = KEY_BOARD_DATA + boardDataSetDTO.getVersion();
        redisTemplate.opsForHash()
                .put(key,
                        boardDataSetDTO.getType().toString(),
                        JsonUtils.toJsonStr(boardDataSetDTO.getData()));
    }
}
