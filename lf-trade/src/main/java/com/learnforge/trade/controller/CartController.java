package com.learnforge.trade.controller;


import com.learnforge.trade.domain.dto.CartsAddDTO;
import com.learnforge.trade.domain.vo.CartVO;
import com.learnforge.trade.service.ICartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * Shopping Cart Item Info, Which is the Course in the Shopping Cart Frontend Controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-28
 */
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Api(tags = "Shopping Cart Related Interfaces")
public class CartController {

    private final ICartService cartService;

    @ApiOperation("Add Course to Shopping Cart")
    @PostMapping
    public void addCourse2Cart(@Valid @RequestBody CartsAddDTO cartsAddDTO){
        cartService.addCourse2Cart(cartsAddDTO.getCourseId());
    }

    @ApiOperation("Get Courses in Shopping Cart")
    @GetMapping
    public List<CartVO> getMyCarts(){
        return cartService.getMyCarts();
    }

    @ApiOperation("Delete Specific Shopping Cart Item")
    @DeleteMapping("/{id}")
    public void deleteCartById(@ApiParam("Shopping Cart Item ID") @PathVariable("id") Long id){
        cartService.deleteCartById(id);
    }

    @ApiOperation("Batch Delete Shopping Cart Items")
    @DeleteMapping
    public void deleteCartById(@ApiParam("Shopping Cart Item ID Collection") @RequestParam("ids") List<Long> ids){
        cartService.deleteCartByIds(ids);
    }
}
