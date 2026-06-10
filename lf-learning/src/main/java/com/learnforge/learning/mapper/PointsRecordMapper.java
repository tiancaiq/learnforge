package com.learnforge.learning.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.learnforge.learning.domain.po.PointsRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-28
 */
public interface PointsRecordMapper extends BaseMapper<PointsRecord> {


    @Select("SELECT  SUM(points) FROM points_record ${ew.customSqlSegment}")
    Integer queryUserPointsByTypeAndDate(@Param(Constants.WRAPPER) QueryWrapper<PointsRecord> wrapper);
    @Select("SELECT  type , SUM(points) AS points FROM points_record ${ew.customSqlSegment} GROUP BY type")
    List<PointsRecord> queryUserPointsDate(@Param(Constants.WRAPPER) QueryWrapper<PointsRecord> wrapper);
}
