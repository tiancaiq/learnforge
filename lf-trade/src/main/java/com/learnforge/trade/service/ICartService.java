package com.learnforge.trade.service;

import com.learnforge.trade.domain.po.Cart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.trade.domain.vo.CartVO;

import java.util.List;

/**
 * <p>
 * Shopping cart item information, which is the course in the shopping cart Service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-28
 */
public interface ICartService extends IService<Cart> {

    void addCourse2Cart(Long courseId);

    List<CartVO> getMyCarts();

    void deleteCartById(Long id);

    void deleteCartByIds(List<Long> ids);

    void deleteCartByUserAndCourseIds(Long userId, List<Long> courseIds);
}
