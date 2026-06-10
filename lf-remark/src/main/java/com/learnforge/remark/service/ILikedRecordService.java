package com.learnforge.remark.service;

import com.learnforge.remark.domain.dto.LikeRecordFormDTO;
import com.learnforge.remark.domain.po.LikedRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Like Record Table Service Class
 * </p>
 *
 * @author luke
 * @since 2026-05-26
 */
public interface ILikedRecordService extends IService<LikedRecord> {

    void addLikeRecord(@Valid LikeRecordFormDTO recordDTO);

    Set<Long> isBizLiked(List<Long> bizIds);

    void readLikedTimesAndSendMessage(String bizType, int maxBizSize);
}
