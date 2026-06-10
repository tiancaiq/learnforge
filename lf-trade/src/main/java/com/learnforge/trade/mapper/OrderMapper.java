package com.learnforge.trade.mapper;

import com.learnforge.trade.domain.po.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * Order Mapper interface
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
public interface OrderMapper extends com.baomidou.mybatisplus.core.mapper.BaseMapper<Order> {

    Order getById(Long id);
}
