package com.learnforge.learning.handler;

import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.DateUtils;
import com.learnforge.learning.constants.RedisConstants;
import com.learnforge.learning.domain.po.PointsBoard;
import com.learnforge.learning.service.IPointsBoardSeasonService;
import com.learnforge.learning.service.IPointsBoardService;
import com.learnforge.learning.utils.TableInfoContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PointsBoardPersistentHandler {

    private final IPointsBoardService pointsBoardService;

    private final IPointsBoardSeasonService seasonService;

    private final StringRedisTemplate redisTemplate;


    @XxlJob("createTableJob")
    public void createPointsBoardTableOfLastSeason(){
        //get last week time
        LocalDateTime time = LocalDateTime.now().minusMonths(1);
        // query season id
        Integer season = seasonService.querySeasonByTime(time);
        if(season == null){
            return;
        }
        //table

        pointsBoardService.createPointsBoardTableBySeason(season);
    }

    @XxlJob("savePointsBoard2DB")
    public void savePointsBoard2DB(){
        //get last week time
        LocalDateTime time = LocalDateTime.now().minusMonths(1);

        //get season data
        Integer season = seasonService.querySeasonByTime(time);
        if (season == null) {
            return;
        }
        //save into threadLocal
        TableInfoContext.set("points_board_" + season);
        try {
            String key = RedisConstants.POINTS_BOARD_KEY_PREFIX + time.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);
            int index = XxlJobHelper.getShardIndex();
            int total = XxlJobHelper.getShardTotal();

            int pageNo = index + 1;
            int pageSize = 1000;
            while(true){
                List<PointsBoard> boardList = pointsBoardService.queryCurrentBoardList(key, pageNo, pageSize);
                if (CollUtils.isEmpty(boardList)) {
                    break;
                }

                boardList.forEach(board -> {
                    board.setId(board.getRank().longValue());
                    board.setRank(null);
                });

                pointsBoardService.saveBatch(boardList);
                pageNo += total;
            }
        } finally {
            TableInfoContext.remove();
        }
    }


    @XxlJob("clearPointsBoardFromRedis")
    public void clearPointsBoardFromReids(){
        // get key
        LocalDateTime time = LocalDateTime.now().minusMonths(1);
        String key = RedisConstants.POINTS_BOARD_KEY_PREFIX + time.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);

        redisTemplate.unlink(key);
    }

}
