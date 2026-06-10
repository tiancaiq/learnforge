package com.learnforge.learning.service.impl;

import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.DateUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.learning.constants.RedisConstants;
import com.learnforge.learning.domain.po.PointsBoard;
import com.learnforge.learning.domain.query.PointsBoardQuery;
import com.learnforge.learning.domain.vo.PointsBoardItemVO;
import com.learnforge.learning.domain.vo.PointsBoardVO;
import com.learnforge.learning.mapper.PointsBoardMapper;
import com.learnforge.learning.service.IPointsBoardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.learning.utils.TableInfoContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-28
 */
@Service
@RequiredArgsConstructor
public class PointsBoardServiceImpl extends ServiceImpl<PointsBoardMapper, PointsBoard> implements IPointsBoardService {

    private final StringRedisTemplate redisTemplate;
    private final UserClient userClient;


    @Override
    public PointsBoardVO queryPointsBoardBySeason(PointsBoardQuery query) {
        //1. check if current season
        Long season = query.getSeason();
        boolean isCurrent = season == null || season == 0;

        LocalDateTime now = LocalDateTime.now();
        String key = RedisConstants.POINTS_BOARD_KEY_PREFIX + now.format(DateUtils.POINTS_BOARD_SUFFIX_FORMATTER);
        //1. query my points and rank

        PointsBoard myBoard = isCurrent ?
                queryMyCurrentBoard(key):  // redis
                queryMyHistoryBoard(season); // mysql

        //2. query ranking list

        List<PointsBoard> list = isCurrent?
                queryCurrentBoardList(key, query.getPageNo(), query.getPageSize()):
                queryHistoryBoardList(query);
        //3, vo
        // mine
        PointsBoardVO vo = new PointsBoardVO();

        if (myBoard != null) {
            vo.setPoints(myBoard.getPoints());
            vo.setRank(myBoard.getRank());
        }

        if(CollUtils.isEmpty(list)){
            return vo;
        }
        //4.2 get users info
        Set<Long> uIds = list.stream().map(PointsBoard::getUserId).collect(Collectors.toSet());
        List<UserDTO> users = userClient.queryUserByIds(uIds);
        Map<Long, String> userMap = new HashMap<>(uIds.size());
        if(CollUtils.isNotEmpty(users)){
            userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
        }

        List<PointsBoardItemVO> items = new ArrayList<>(list.size());
        for (PointsBoard p : list) {
            PointsBoardItemVO v = new PointsBoardItemVO();
            items.add(v);
            v.setPoints(p.getPoints());
            v.setRank(p.getRank());
            v.setName(userMap.get(p.getUserId()));
        }


        vo.setBoardList(items);
        return vo;
    }

    @Override
    public void createPointsBoardTableBySeason(Integer season) {
        getBaseMapper().createPointsBoardTable("points_board_" +season);
    }

    private List<PointsBoard> queryHistoryBoardList(PointsBoardQuery query) {
        TableInfoContext.set("points_board_" + query.getSeason());
        try {
            List<PointsBoard> records = lambdaQuery()
                    .select(PointsBoard::getId, PointsBoard::getUserId, PointsBoard::getPoints)
                    .page(query.<PointsBoard>toMpPage())
                    .getRecords();
            if (CollUtils.isEmpty(records)) {
                return CollUtils.emptyList();
            }
            records.forEach(record -> record.setRank(record.getId().intValue()));
            return records;
        } finally {
            TableInfoContext.remove();
        }
    }
    @Override
    public List<PointsBoard> queryCurrentBoardList(
            String key,
            @Min(value = 1, message = "Page number must be at least 1") Integer pageNo,
            @Min(value = 1, message = "Page size must be at least 1") Integer pageSize) {
        int from = (pageNo - 1) * pageSize;
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, from, pageSize + from - 1);

        if(CollUtils.isEmpty(tuples)){
            return  CollUtils.emptyList();
        }

        int rank = from + 1;
        List<PointsBoard> list = new ArrayList<>(tuples.size());

        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            String userId = tuple.getValue();
            Double points = tuple.getScore();
            if(userId == null || points == null){
                continue;
            }
            PointsBoard p = new PointsBoard();
            p.setUserId(Long.valueOf(userId));
            p.setPoints(points.intValue());
            p.setRank(rank++);
            list.add(p);
        }

        return list;
    }

    private PointsBoard queryMyHistoryBoard(Long season) {
        TableInfoContext.set("points_board_" + season);
        try {
            PointsBoard board = lambdaQuery()
                    .select(PointsBoard::getId, PointsBoard::getUserId, PointsBoard::getPoints)
                    .eq(PointsBoard::getUserId, UserContext.getUser())
                    .one();
            if (board != null) {
                board.setRank(board.getId().intValue());
            }
            return board;
        } finally {
            TableInfoContext.remove();
        }
    }

    private PointsBoard queryMyCurrentBoard(String key) {
        // binding key
        BoundZSetOperations<String, String> ops = redisTemplate.boundZSetOps(key);

        String userId = UserContext.getUser().toString();

        // query point

        Double points = ops.score(userId);
        // query ranking
        Long rank = ops.reverseRank(userId);

        PointsBoard p = new PointsBoard();
        p.setPoints(points == null ? 0 : points.intValue());
        p.setRank(rank == null ? 0 : rank.intValue() +1);

        // return
        return p;
    }
}
