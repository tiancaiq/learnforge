package com.learnforge.learning.controller;


import com.learnforge.learning.domain.query.PointsBoardQuery;
import com.learnforge.learning.domain.vo.PointsBoardVO;
import com.learnforge.learning.service.IPointsBoardService;
import com.learnforge.learning.service.IPointsRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-28
 */
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
@Api(tags = "points relate")
public class PointsBoardController {

    private final IPointsBoardService pointsBoardService;

    @GetMapping
    @ApiOperation("ranking system")
    public PointsBoardVO queryPointsBoardBySeason(PointsBoardQuery query){
        return pointsBoardService.queryPointsBoardBySeason(query);
    }

}
