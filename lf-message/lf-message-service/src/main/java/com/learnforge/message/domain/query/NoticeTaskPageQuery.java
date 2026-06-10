package com.learnforge.message.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "Notification template query object")
@Data
public class NoticeTaskPageQuery extends PageQuery {
    private Boolean finished;
    private String keyword;
    private LocalDateTime minPushTime;
    private LocalDateTime maxPushTime;
}
