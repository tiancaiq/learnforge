package com.learnforge.common.domain.query;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learnforge.common.constants.Constant;
import com.learnforge.common.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;

@Data
@ApiModel(description = "Pagination request parameters")
@Accessors(chain = true)
public class PageQuery {
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final Integer DEFAULT_PAGE_NUM = 1;

    @ApiModelProperty(value = "Page number", example = "1")
    @Min(value = 1, message = "Page number cannot be less than 1")
    private Integer pageNo = DEFAULT_PAGE_NUM;

    @ApiModelProperty(value = "Page size", example = "5")
    @Min(value = 1, message = "Number of items per page cannot be less than 1")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    @ApiModelProperty(value = "Sort in ascending order", example = "true")
    private Boolean isAsc = true;

    @ApiModelProperty(value = "Sort field", example = "id")
    private String sortBy;

    public int from(){
        return (pageNo - 1) * pageSize;
    }

    public <T> Page<T> toMpPage(OrderItem ... orderItems) {
        Page<T> page = new Page<>(pageNo, pageSize);
        // Whether manually specify sort method
        if (orderItems != null && orderItems.length > 0) {
            for (OrderItem orderItem : orderItems) {
                page.addOrder(orderItem);
            }
            return page;
        }
        // Whether front-end has sort field
        if (StringUtils.isNotEmpty(sortBy)){
            OrderItem orderItem = new OrderItem();
            orderItem.setAsc(isAsc);
            orderItem.setColumn(sortBy);
            page.addOrder(orderItem);
        }
        return page;
    }

    public <T> Page<T> toMpPage(String defaultSortBy, boolean isAsc) {
        if (StringUtils.isBlank(sortBy)){
            sortBy = defaultSortBy;
            this.isAsc = isAsc;
        }
        Page<T> page = new Page<>(pageNo, pageSize);
        OrderItem orderItem = new OrderItem();
        orderItem.setAsc(this.isAsc);
        orderItem.setColumn(sortBy);
        page.addOrder(orderItem);
        return page;
    }
    public <T> Page<T> toMpPageDefaultSortByCreateTimeDesc() {
        return toMpPage(Constant.DATA_FIELD_NAME_CREATE_TIME, false);
    }
}
