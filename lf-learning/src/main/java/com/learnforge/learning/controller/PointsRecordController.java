package com.learnforge.learning.controller;


import com.learnforge.learning.domain.vo.PointsStatisticsVO;
import com.learnforge.learning.service.IPointsRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-28
 */
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointsRecordController {
    private final IPointsRecordService pointsRecordService;

    @GetMapping("today")
    public List<PointsStatisticsVO> queryMyPointsToday() {
        return pointsRecordService.queryMyPointsToday();
    }
}
