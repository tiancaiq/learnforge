package com.learnforge.message.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Notification template query object")
@Data
public class UserInboxQuery extends PageQuery {
    private Boolean isRead;
    private Integer type;
}
