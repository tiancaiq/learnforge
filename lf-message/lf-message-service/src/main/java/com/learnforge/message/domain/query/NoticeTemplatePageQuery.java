package com.learnforge.message.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Notification template query object")
@Data
public class NoticeTemplatePageQuery extends PageQuery {
    private Integer status;
    private String keyword;
}
