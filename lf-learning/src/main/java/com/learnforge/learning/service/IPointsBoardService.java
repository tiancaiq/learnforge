package com.learnforge.learning.service;

import com.learnforge.learning.domain.po.PointsBoard;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.learning.domain.query.PointsBoardQuery;
import com.learnforge.learning.domain.vo.PointsBoardVO;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-28
 */
public interface IPointsBoardService extends IService<PointsBoard> {

    PointsBoardVO queryPointsBoardBySeason(PointsBoardQuery query);

    void createPointsBoardTableBySeason(Integer season);

    List<PointsBoard> queryCurrentBoardList(
            String key,
            @Min(value = 1, message = "Page number must be at least 1") Integer pageNo,
            @Min(value = 1, message = "Page size must be at least 1") Integer pageSize);
}
