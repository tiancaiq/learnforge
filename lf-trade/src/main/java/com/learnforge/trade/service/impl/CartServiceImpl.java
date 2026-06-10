package com.learnforge.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.course.CourseClient;
import com.learnforge.api.dto.course.CourseFullInfoDTO;
import com.learnforge.api.dto.course.CourseSimpleInfoDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.trade.config.TradeProperties;
import com.learnforge.trade.domain.po.Cart;
import com.learnforge.trade.domain.vo.CartVO;
import com.learnforge.trade.mapper.CartMapper;
import com.learnforge.trade.service.ICartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.learnforge.trade.constants.TradeErrorInfo.*;

/**
 * <p>
 * Shopping cart item information, which is the course in the shopping cart Service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    private final CourseClient courseClient;
    private final TradeProperties tradeProperties;

    @Override
    public void addCourse2Cart(Long courseId) {
        Long userId = UserContext.getUser();
        log.debug("Add to cart request: User: {}, Course: {}", userId, courseId);
        // 1. Query whether the course is already in the cart
        if (checkCourseExists(courseId, userId)) {
            return;
        }
        // 2. Query whether the course in the cart exceeds the limit
        checkCartsFull(userId);

        // 3. Query course information by id
        CourseFullInfoDTO courseInfo = courseClient.getCourseInfoById(courseId, false, false);

        // 4. Judge whether it is empty
        if (courseInfo == null) {
            throw new BadRequestException(COURSE_NOT_EXISTS);
        }

        // 5. Judge whether it is expired
        if (courseInfo.getPurchaseEndTime().isBefore(LocalDateTime.now())) {
            // Already expired, cannot purchase
            throw new BadRequestException(COURSE_EXPIRED);
        }
        // 5. Write to cart
        Cart cart = new Cart();
        cart.setId(IdWorker.getId()); //Shopping cart id
        cart.setCourseId(courseId); //Course ID
        cart.setCourseName(courseInfo.getName());
        cart.setUserId(UserContext.getUser());
        cart.setCoverUrl(courseInfo.getCoverUrl());
        cart.setPrice(courseInfo.getPrice());
        save(cart);
        log.debug("Add to cart successfully! User: {}, Course: {}", userId, courseId);
    }

    private void checkCartsFull(Long userId) {
        int count = lambdaQuery().eq(Cart::getUserId, userId).count();
        if (count >= tradeProperties.getMaxCourseAmount()) {
            throw new BizIllegalException(
                    StringUtils.format(CARTS_FULL, tradeProperties.getMaxCourseAmount()));
        }
    }

    private boolean checkCourseExists(Long courseId, Long userId) {
        int count = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getCourseId, courseId)
                .count();
        return count > 0;
    }

    @Override
    public List<CartVO> getMyCarts() {
        // 1. Get user
        Long userId = UserContext.getUser();
        // 2. Query my shopping cart
        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, userId).list();
        if (CollUtils.isEmpty(carts)) {
            return CollUtils.emptyList();
        }
        // 3. Query courses in the shopping cart
        List<Long> courseIds = carts.stream().map(Cart::getCourseId).collect(Collectors.toList());
        List<CourseSimpleInfoDTO> courseSimpleInfos = courseClient.getSimpleInfoList(courseIds);
        Map<Long, CourseSimpleInfoDTO> map = courseSimpleInfos.stream()
                .collect(Collectors.toMap(CourseSimpleInfoDTO::getId, c -> c));
        // 4. Organize vo
        List<CartVO> list = new ArrayList<>(carts.size());
        for (Cart cart : carts) {
            // 4.1. Convert VO
            CartVO vo = BeanUtils.toBean(cart, CartVO.class);
            list.add(vo);
            // 4.2. Get new course information
            CourseSimpleInfoDTO info = map.get(cart.getCourseId());
            vo.setNowPrice(info.getPrice());
            vo.setExpired(info.getPurchaseEndTime().isBefore(LocalDateTime.now()));
            vo.setCourseValidDate(info.getPurchaseEndTime());
        }
        // 5. Sort
        return list.stream().sorted(
                Comparator.comparing(CartVO::getExpired).reversed() // First check if it is expired, non-expired ones come first, expired ones come later
                        .thenComparingLong(CartVO::getId).reversed() // Then check id, the one with larger id is newly added
        ).collect(Collectors.toList());
    }

    @Override
    public void deleteCartById(Long id) {
        // 1. Get user
        Long userId = UserContext.getUser();
        // 2. Delete
        remove(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getId, id)
                .eq(Cart::getUserId, userId)
        );
    }

    @Override
    public void deleteCartByIds(List<Long> ids) {
        Long userId = UserContext.getUser();
        remove(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .in(Cart::getId, ids)
        );
    }

    @Override
    public void deleteCartByUserAndCourseIds(Long userId, List<Long> courseIds) {
        log.debug("Try to remove the purchased course from the cart, user id: {}, course id: {}", userId, courseIds);
        try {
            if(CollUtils.isEmpty(courseIds) || userId == null){
                return;
            }
            remove(new LambdaQueryWrapper<Cart>()
                    .eq(Cart::getUserId, userId)
                    .in(Cart::getCourseId, courseIds)
            );
        } catch (Exception e) {
            log.error("Removing purchased course from cart occurred an exception, user id: {}, course id: {}", userId, courseIds, e);
        }
    }
}
