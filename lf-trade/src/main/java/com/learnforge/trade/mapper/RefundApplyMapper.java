package com.learnforge.trade.mapper;

import com.learnforge.trade.domain.po.RefundApply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * Refund application Mapper interface
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
public interface RefundApplyMapper extends BaseMapper<RefundApply> {

    @Select("SELECT id FROM refund_apply WHERE status = 1 LIMIT 1")
    Long nextRefundApplyToApprove();

    List<RefundApply> queryByDetailId(Long detailId);
}
