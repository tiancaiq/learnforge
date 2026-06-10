package com.learnforge.api.dto.trade;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderBasicDTO {
    /**
     * Order id
     */
    private Long orderId;
    /**
     * User id who placed the order
     */
    private Long userId;
    /**
     * Set of course ids ordered
     */
    private List<Long> courseIds;
    /**
     * Order completion time
     */
    private LocalDateTime finishTime;
}
