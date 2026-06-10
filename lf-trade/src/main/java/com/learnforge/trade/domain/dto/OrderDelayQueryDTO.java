package com.learnforge.trade.domain.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * MQ Notification Message for Delayed Order Status Query
 */
@Data
public class OrderDelayQueryDTO {
    /**
     * Order id
     */
    private Long orderId;
    /**
     * Delay Notification Interval, Default is 3 Seconds, 5 Seconds, 15 Seconds, 30 Seconds, 60 Seconds, 2 Minutes, Total 6 Delay Queries, Can Cancel Task Immediately After Success
     */
    private List<Long> delayMillis;

    public static OrderDelayQueryDTO init(Long orderId){
        OrderDelayQueryDTO dto = new OrderDelayQueryDTO();
        dto.setOrderId(orderId);
        List<Long> list = new ArrayList<>(6);
        list.add(3000L);
        list.add(5000L);
        list.add(15000L);
        list.add(30000L);
        list.add(60000L);
        list.add(120000L);
        dto.setDelayMillis(list);
        return dto;
    }
    public long removeFirst(){
        return delayMillis.remove(0);
    }
}
