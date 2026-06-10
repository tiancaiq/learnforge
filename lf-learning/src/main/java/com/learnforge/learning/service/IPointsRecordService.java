package com.learnforge.learning.service;

import com.learnforge.learning.domain.enums.PointsRecordType;
import com.learnforge.learning.domain.po.PointsRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.learning.domain.vo.PointsStatisticsVO;

import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-28
 */
public interface IPointsRecordService extends IService<PointsRecord> {

    void addPointsRecord(Long userId, int points, PointsRecordType recordType);

    List<PointsStatisticsVO> queryMyPointsToday();
}
