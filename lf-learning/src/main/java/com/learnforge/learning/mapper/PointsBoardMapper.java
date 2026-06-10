package com.learnforge.learning.mapper;

import com.learnforge.learning.domain.po.PointsBoard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-28
 */
public interface PointsBoardMapper extends BaseMapper<PointsBoard> {

    void createPointsBoardTable(@Param("tableName") String tableName);
}
