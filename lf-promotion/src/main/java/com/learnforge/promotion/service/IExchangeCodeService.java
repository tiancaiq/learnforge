package com.learnforge.promotion.service;

import com.learnforge.promotion.domain.po.Coupon;
import com.learnforge.promotion.domain.po.ExchangeCode;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-02
 */
public interface IExchangeCodeService extends IService<ExchangeCode> {

    void asyncGenerateCode(Coupon coupon);

    boolean updateExchangeMark(long serialNum, boolean b);
}
